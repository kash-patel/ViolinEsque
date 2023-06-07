package com.kashithekash.violinesque

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kashithekash.violinesque.navigation.AudioSettings
import com.kashithekash.violinesque.navigation.InterfaceConfig
import com.kashithekash.violinesque.navigation.Play
import com.kashithekash.violinesque.navigation.Calibration
import com.kashithekash.violinesque.navigation.violinEsqueScreens
import com.kashithekash.violinesque.ui.audioSettings.AudioSettingsScreen
import com.kashithekash.violinesque.ui.components.NavBar
import com.kashithekash.violinesque.ui.components.PositionIndicatorRail
import com.kashithekash.violinesque.ui.interfaceConfig.InterfaceConfigScreen
import com.kashithekash.violinesque.ui.play.PlayScreen
import com.kashithekash.violinesque.viewmodels.InterfaceConfigViewModel
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme
import com.kashithekash.violinesque.ui.calibration.CalibrationScreen
import com.kashithekash.violinesque.utility.Config
import com.kashithekash.violinesque.utility.PrefRepo
import com.kashithekash.violinesque.utility.SoundManagerStringBased
import com.kashithekash.violinesque.viewmodels.AudioSettingsViewModel
import com.kashithekash.violinesque.viewmodels.OrientationViewModel

class ViolinEsqueActivity : ComponentActivity() {

    private lateinit var orientationViewModel: OrientationViewModel
    private lateinit var interfaceConfigViewModel: InterfaceConfigViewModel
    private lateinit var audioSettingsViewModel: AudioSettingsViewModel
    private lateinit var soundManagerStringBased: SoundManagerStringBased
    private lateinit var prefRepo: PrefRepo

    override fun onCreate(savedInstanceState: Bundle?) {

        prefRepo = PrefRepo(this)

        Config.init(
            savedFadeInTime = prefRepo.getFadeInTime(),
            savedBlendTime = prefRepo.getBlendTime(),
            savedFadeOutTime = prefRepo.getFadeOutTime(),
            savedFadeOutDelay = prefRepo.getFadeOutDelay(),
            savedExpandButtons = prefRepo.getExpandButtons(),
            savedInvertRoll = prefRepo.getInvertRoll(),
            savedInvertPitch = prefRepo.getInvertPitch(),
            savedRollCentre = prefRepo.getRollCentre(),
            savedStringRollRange = prefRepo.getStringRollRange(),
            savedPitchCentre = prefRepo.getPitchCentre(),
            savedTotalPitchRange = prefRepo.getTotalPitchRange(),
            savedHandPositionsList = prefRepo.getHandPositionsList(),
        )

        soundManagerStringBased = SoundManagerStringBased(this)

        orientationViewModel =
            ViewModelProvider(this)[OrientationViewModel(application)::class.java]
        interfaceConfigViewModel =
            ViewModelProvider(this)[InterfaceConfigViewModel(application)::class.java]
        audioSettingsViewModel =
            ViewModelProvider(this)[AudioSettingsViewModel(application)::class.java]

        orientationViewModel.setSoundManager(soundManagerStringBased)
        orientationViewModel.setPrefRepo(prefRepo)
        interfaceConfigViewModel.setSoundManager(soundManagerStringBased)
        interfaceConfigViewModel.setPrefRepo(prefRepo)
        audioSettingsViewModel.setSoundManager(soundManagerStringBased)
        audioSettingsViewModel.setPrefRepo(prefRepo)

        orientationViewModel.monitorStrings()
        orientationViewModel.rotationReader.observe(this) {
            orientationViewModel.updateRoll(it[2], it[1])
            orientationViewModel.updatePitch(it[1], it[2])
        }

        super.onCreate(savedInstanceState)
        setContent {
            ViolinEsqueApp(
                orientationViewModel,
                interfaceConfigViewModel,
                audioSettingsViewModel
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationViewModel.releaseResources()
    }
}

@Composable
fun ViolinEsqueApp (
    orientationViewModel: OrientationViewModel,
    interfaceConfigViewModel: InterfaceConfigViewModel,
    audioSettingsViewModel: AudioSettingsViewModel
) {

    Log.w("ViolinEsqueActivity", "Launched!")

    ViolinEsqueTheme {

        val navController: NavHostController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen = violinEsqueScreens.find { it.route == currentDestination?.route } ?: Play

        Surface (color = ViolinEsqueTheme.colors.background) {

            Row(Modifier.fillMaxSize()) {

                NavBar(
                    allScreens = violinEsqueScreens,
                    onTabSelect = { screen -> navController.navigateSingleTopTo(screen.route) },
                    currentScreen = currentScreen
                )

                PositionIndicatorRail(
                    invertPitchLiveData = orientationViewModel.invertPitchLiveData,
                    handPositionsMutableList = interfaceConfigViewModel.handPositionsMutableList,
                    currentHandPositionIndexLiveData = orientationViewModel.currentHandPositionIndexLiveData
                )

                ViolinEsqueNavHost(
                    orientationViewModel = orientationViewModel,
                    interfaceConfigViewModel = interfaceConfigViewModel,
                    audioSettingsViewModel = audioSettingsViewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ViolinEsqueNavHost(
    orientationViewModel: OrientationViewModel,
    interfaceConfigViewModel: InterfaceConfigViewModel,
    audioSettingsViewModel: AudioSettingsViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Play.route,
        modifier = modifier
    ) {

        composable(route = Play.route) {
            PlayScreen(
                currentStringLiveData = orientationViewModel.currentStringLiveData,
                currentHandPositionIndexLiveData = orientationViewModel.currentHandPositionIndexLiveData,
                onButtonTouch = { n -> interfaceConfigViewModel.buttonTouched(n) },
                onButtonRelease = { n -> interfaceConfigViewModel.buttonReleased(n) },
                expandButtonsLiveData = interfaceConfigViewModel.expandButtonsLiveData,
                handPositionsListMutable = interfaceConfigViewModel.handPositionsMutableList
            )
        }

        composable(route = InterfaceConfig.route) {
            InterfaceConfigScreen(
                expandButtonsLiveData = interfaceConfigViewModel.expandButtonsLiveData,
                onSetExpandButtons = { newExpandButtons -> interfaceConfigViewModel.setExpandButtons(newExpandButtons) },
                handPositionsMutableList = interfaceConfigViewModel.handPositionsMutableList,
                addHandPostion = { i, n -> interfaceConfigViewModel.addHandPosition(i, n) },
                removeHandPosition = { i -> interfaceConfigViewModel.removeHandPosition(i) },
                changeHandPosition = { i, n -> interfaceConfigViewModel.changeHandPosition(i, n) }
            )
        }

        composable(route = AudioSettings.route) {
            AudioSettingsScreen(
                getFadeInTime = { audioSettingsViewModel.getFadeInTime() },
                onFadeInTimeChange = { newFadeInTime -> audioSettingsViewModel.setFadeInTime(newFadeInTime) },
                getBlendTime = { audioSettingsViewModel.getBlendTime() },
                onBlendTimeChange = { newBlendTime -> audioSettingsViewModel.setBlendTime(newBlendTime) },
                getFadeOutTime = { audioSettingsViewModel.getFadeOutTime() },
                onFadeOutTimeChange = { newFadeOutTime -> audioSettingsViewModel.setFadeOutTime(newFadeOutTime) },
                getFadeOutDelay = { audioSettingsViewModel.getFadeOutDelay() },
                onFadeOutDelayChange = { newFadeOutDelay -> audioSettingsViewModel.setFadeOutDelay(newFadeOutDelay) },
            )
        }

        composable(route = Calibration.route) {
            CalibrationScreen(
                currentStringLiveData = orientationViewModel.currentStringLiveData,
                invertRollLiveData = orientationViewModel.invertRollLiveData,
                toggleInvertRoll = { orientationViewModel.toggleInvertRoll() },
                invertPitchLiveData = orientationViewModel.invertPitchLiveData,
                toggleInvertPitch = { orientationViewModel.toggleInvertPitch() },
                setGDRollPoint = { orientationViewModel.setGDRollPoint() },
                setAERollPoint = { orientationViewModel.setAERollPoint() },
                resetRollPoints = { orientationViewModel.resetRollPoints() },
                setTiltAwayLimit = { orientationViewModel.setTiltAwayLimit() },
                setTiltTowardLimit = { orientationViewModel.setTiltTowardLimit() },
                resetTiltLimits = { orientationViewModel.resetTiltLimits() }
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo (route: String) {
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}