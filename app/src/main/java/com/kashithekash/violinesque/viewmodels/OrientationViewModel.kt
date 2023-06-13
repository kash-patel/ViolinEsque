package com.kashithekash.violinesque.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kashithekash.violinesque.utility.Config
import com.kashithekash.violinesque.utility.HandPositionManager
import com.kashithekash.violinesque.utility.Pi
import com.kashithekash.violinesque.utility.PrefRepo
import com.kashithekash.violinesque.utility.RotationReader
import com.kashithekash.violinesque.utility.SoundManagerStringBased
import com.kashithekash.violinesque.utility.StringManager
import com.kashithekash.violinesque.utility.ViolinString
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

class OrientationViewModel(application: Application) : AndroidViewModel(application) {

    val rotationReader: RotationReader = RotationReader(context = application)

    private val stringManager: StringManager = StringManager()
    private val handPositionManager: HandPositionManager = HandPositionManager()
    private lateinit var soundManagerStringBased: SoundManagerStringBased

    private var cachedString: ViolinString? = null
    private val _currentStringLiveData: MutableLiveData<ViolinString> = MutableLiveData(null)
    val currentStringLiveData: LiveData<ViolinString> = _currentStringLiveData

    private var cachedHandPositionIndex: Int = 0
    private val _currentHandPositionIndexLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val currentHandPositionIndexLiveData: LiveData<Int> = _currentHandPositionIndexLiveData

    val invertRollLiveData: MutableLiveData<Boolean> = MutableLiveData(Config.invertRoll)
    val invertPitchLiveData: MutableLiveData<Boolean> = MutableLiveData(Config.invertPitch)

    private var currentRoll: Float = 0f
    private var currentPitch: Float = 0f

    private var GDRollPoint: Float = -Pi / 6
    private var AERollPoint: Float = Pi / 6

    private var lowestPositionPitch: Float = 0f
    private var highestPositionPitch: Float = -Pi / 4

    private lateinit var prefRepo: PrefRepo

    fun updateRoll (roll: Float, pitch: Float) {
        currentRoll = roll
        _currentStringLiveData.value = stringManager.calculateCurrentString(roll)
        if (_currentStringLiveData.value != cachedString) {
            cachedString = _currentStringLiveData.value
            soundManagerStringBased.handleStringChange(cachedString!!)
        }
    }

    fun updatePitch (pitch: Float, roll: Float) {
        currentPitch = if (abs(roll) >= Pi / 2) pitch.sign * Pi - pitch else pitch
        _currentHandPositionIndexLiveData.value = handPositionManager.calculateCurrentHandPositionIndex(currentPitch)
        if (_currentHandPositionIndexLiveData.value != cachedHandPositionIndex) {
            cachedHandPositionIndex = _currentHandPositionIndexLiveData.value!!
            soundManagerStringBased.handleHandPositionChange(Config.handPositionsList[cachedHandPositionIndex])
        }
    }

    fun handleHandPositionChanged (index: Int, newHandPosition: Int) {
        if (index == _currentHandPositionIndexLiveData.value) soundManagerStringBased.handleHandPositionChange(newHandPosition)
    }

    fun setSoundManager (soundManagerStringBasedInstance: SoundManagerStringBased) {
        soundManagerStringBased = soundManagerStringBasedInstance
    }

    fun setPrefRepo (prefRepoInstance: PrefRepo) {
        prefRepo = prefRepoInstance
    }

    fun monitorStrings () {
        viewModelScope.launch { soundManagerStringBased.manageGString() }
        viewModelScope.launch { soundManagerStringBased.manageDString() }
        viewModelScope.launch { soundManagerStringBased.manageAString() }
        viewModelScope.launch { soundManagerStringBased.manageEString() }
    }

    fun releaseResources () {
        soundManagerStringBased.release()
    }

//    fun toggleInvertPitch () {
//        invertPitchLiveData.value = !invertPitchLiveData.value!!
//        Config.setInvertPitch(!Config.invertPitch)
//        prefRepo.setInvertPitch(Config.invertPitch)
//    }

    private fun setInvertPitch (newInvertPitch: Boolean) {
        invertPitchLiveData.value = newInvertPitch
        Config.setInvertPitch(newInvertPitch)
        prefRepo.setInvertPitch(newInvertPitch)
    }

//    fun toggleInvertRoll () {
//        invertRollLiveData.value = !invertRollLiveData.value!!
//        Config.setInvertRoll(!Config.invertRoll)
//        prefRepo.setInvertRoll(Config.invertRoll)
//    }

    private fun setInvertRoll (newInvertRoll: Boolean) {
        invertRollLiveData.value = newInvertRoll
        Config.setInvertRoll(newInvertRoll)
        prefRepo.setInvertRoll(newInvertRoll)
    }

    fun setGDRollPoint () {

        GDRollPoint = currentRoll
        Config.setRollCentre((GDRollPoint + AERollPoint) / 2)
        Config.setStringRollRange(abs(AERollPoint - GDRollPoint) / 3)
        prefRepo.setRollCentre(Config.rollCentre)
        prefRepo.setStringRollRange(Config.stringRollRange)

        setInvertRoll(GDRollPoint > AERollPoint)
    }

    fun setAERollPoint () {

        AERollPoint = currentRoll
        Config.setRollCentre((GDRollPoint + AERollPoint) / 2)
        Config.setStringRollRange(abs(AERollPoint - GDRollPoint) / 3)
        prefRepo.setRollCentre(Config.rollCentre)
        prefRepo.setStringRollRange(Config.stringRollRange)

        setInvertRoll(GDRollPoint > AERollPoint)
    }

    fun resetRollPoints () {
        GDRollPoint = -Pi / 6
        AERollPoint = Pi / 6
        Config.setRollCentre((GDRollPoint + AERollPoint) / 2)
        Config.setStringRollRange(abs(AERollPoint - GDRollPoint) / 3)
        prefRepo.setRollCentre(Config.rollCentre)
        prefRepo.setStringRollRange(Config.stringRollRange)

        setInvertRoll(false)
    }

    fun setHighestPositionPitch () {
        lowestPositionPitch = currentPitch
        Config.setPitchCentre((lowestPositionPitch + highestPositionPitch) / 2)
        Config.setTotalPitchRange(-abs(highestPositionPitch - lowestPositionPitch))
        prefRepo.setPitchCentre(Config.pitchCentre)
        prefRepo.setTotalPitchRange(Config.totalPitchRange)

        setInvertPitch(highestPositionPitch > lowestPositionPitch)
    }

    fun setLowestPositionPitch () {
        highestPositionPitch = currentPitch
        Config.setPitchCentre((lowestPositionPitch + highestPositionPitch) / 2)
        Config.setTotalPitchRange(-abs(highestPositionPitch - lowestPositionPitch))
        prefRepo.setPitchCentre(Config.pitchCentre)
        prefRepo.setTotalPitchRange(Config.totalPitchRange)

        setInvertPitch(highestPositionPitch > lowestPositionPitch)
    }

    fun resetHandPositionPitchLimits () {
        lowestPositionPitch = 0f
        highestPositionPitch = -Pi / 4
        Config.setPitchCentre((lowestPositionPitch + highestPositionPitch) / 2)
        Config.setTotalPitchRange(-abs(highestPositionPitch - lowestPositionPitch))
        prefRepo.setPitchCentre(Config.pitchCentre)
        prefRepo.setTotalPitchRange(Config.totalPitchRange)

        setInvertPitch(false)
    }

    fun resetAll () {

        AERollPoint = Pi / 6
        GDRollPoint = -Pi / 6
        Config.setRollCentre((GDRollPoint + AERollPoint) / 2)
        Config.setStringRollRange((AERollPoint - GDRollPoint) / 3)
        prefRepo.setRollCentre(Config.rollCentre)
        prefRepo.setStringRollRange(Config.stringRollRange)

        setInvertRoll(false)

        lowestPositionPitch = 0f
        highestPositionPitch = -Pi / 4
        Config.setPitchCentre((lowestPositionPitch + highestPositionPitch) / 2)
        Config.setTotalPitchRange(-abs(highestPositionPitch - lowestPositionPitch))
        prefRepo.setPitchCentre(Config.pitchCentre)
        prefRepo.setTotalPitchRange(Config.totalPitchRange)

        setInvertPitch(false)
    }
}