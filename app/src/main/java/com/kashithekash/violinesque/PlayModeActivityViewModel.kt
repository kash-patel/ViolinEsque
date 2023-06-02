package com.kashithekash.violinesque

import SoundManagerStringBased
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlayModeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val rotationReader : RotationReader = RotationReader(context = application)
    private val stringManager : StringManager = StringManager()
    private val soundManagerStringBased : SoundManagerStringBased = SoundManagerStringBased(context = application)

    private var currentStringLiveData: MutableLiveData<ViolinString> = MutableLiveData<ViolinString>(ViolinString.A)
    private var cachedString: ViolinString? = null

    private val config: Config = Config
    private val prefRepo: PrefRepo = PrefRepo(application.applicationContext)

    fun buttonTouched (buttonNumber: Int) {
        soundManagerStringBased.handleButtonTouch(buttonNumber)
    }

    fun buttonReleased (buttonNumber: Int) {
        soundManagerStringBased.handleButtonRelease(buttonNumber)
    }

    fun stringChanged (newString: ViolinString) {
        soundManagerStringBased.handleStringChange(newString)
    }

    fun monitorStrings () {
        viewModelScope.launch { soundManagerStringBased.manageGString() }
        viewModelScope.launch { soundManagerStringBased.manageDString() }
        viewModelScope.launch { soundManagerStringBased.manageAString() }
        viewModelScope.launch { soundManagerStringBased.manageEString() }
    }

    fun updateRoll (roll : Float) {
        currentStringLiveData.value = stringManager.calculateCurrentString(roll)
        if (currentStringLiveData.value != cachedString) {
            cachedString = currentStringLiveData.value
            stringChanged(cachedString!!)
        }
    }

    fun getCurrentStringLiveData () : LiveData<ViolinString> {
        return currentStringLiveData
    }

    fun getButtonInteractability (buttonNumber: Int) : Interactability {
        return config.buttonInteractabilityArray[buttonNumber]
    }

    fun getRotationVector () = rotationReader

    fun loadConfig () {

        val savedButtonInteractabilities: Array<Interactability> = arrayOf(
            prefRepo.getButtonInteractability(0),
            prefRepo.getButtonInteractability(1),
            prefRepo.getButtonInteractability(2),
            prefRepo.getButtonInteractability(3),
            prefRepo.getButtonInteractability(4),
            prefRepo.getButtonInteractability(5),
            prefRepo.getButtonInteractability(6),
            prefRepo.getButtonInteractability(7),
            prefRepo.getButtonInteractability(8),
            prefRepo.getButtonInteractability(9),
            prefRepo.getButtonInteractability(10),
            prefRepo.getButtonInteractability(11),
            prefRepo.getButtonInteractability(12)
        )

        config.init(savedButtonInteractabilities, prefRepo.getRollCentre(), prefRepo.getStringRollRange())
    }

    fun releaseResources () {
        soundManagerStringBased.release()
    }
}