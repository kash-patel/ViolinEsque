package com.kashithekash.violinesque.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kashithekash.violinesque.utility.Config
import com.kashithekash.violinesque.utility.HandPositionManager
import com.kashithekash.violinesque.utility.PrefRepo
import com.kashithekash.violinesque.utility.RotationReader
import com.kashithekash.violinesque.utility.SoundManagerStringBased
import com.kashithekash.violinesque.utility.StringManager
import com.kashithekash.violinesque.utility.ViolinString
import com.kashithekash.violinesque.utility.handPostionStartIndices
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

    private var cachedPositionIndex: Int = 0
    private val _currentHandPositionIndexLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val currentHandPositionIndexLiveData: LiveData<Int> = _currentHandPositionIndexLiveData

    val invertRollLiveData: MutableLiveData<Boolean> = MutableLiveData(Config.invertRoll)
    val invertPitchLiveData: MutableLiveData<Boolean> = MutableLiveData(Config.invertPitch)

    private var currentRoll: Float = 0f
    private var currentPitch: Float = 0f

    private var GDRollPoint: Float = -45f
    private var AERollPoint: Float = 45f

    private var tiltAwayPitch: Float = 0f
    private var tiltTowardPitch: Float = -90f

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
        currentPitch = if (abs(roll) >= 90) pitch.sign * 180f - pitch else pitch
        _currentHandPositionIndexLiveData.value = handPositionManager.calculateCurrentHandPositionIndex(currentPitch)
        if (_currentHandPositionIndexLiveData.value != cachedPositionIndex) {
            cachedPositionIndex = _currentHandPositionIndexLiveData.value!!
            soundManagerStringBased.handleHandPositionChange(Config.handPositionsList[cachedPositionIndex])
        }
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

    fun toggleInvertPitch () {
        invertPitchLiveData.value = !invertPitchLiveData.value!!
        Config.setInvertPitch(!Config.invertPitch)
        prefRepo.setInvertPitch(Config.invertPitch)
    }

    fun toggleInvertRoll () {
        invertRollLiveData.value = !invertRollLiveData.value!!
        Config.setInvertRoll(!Config.invertRoll)
        prefRepo.setInvertRoll(Config.invertRoll)
    }

    fun setGDRollPoint () {
        GDRollPoint = currentRoll
        Config.setRollCentre((GDRollPoint + AERollPoint) / 2)
        Config.setStringRollRange((AERollPoint - GDRollPoint) / 3)
        prefRepo.setRollCentre(Config.rollCentre)
        prefRepo.setStringRollRange(Config.stringRollRange)
    }

    fun setAERollPoint () {
        AERollPoint = currentRoll
        Config.setRollCentre((GDRollPoint + AERollPoint) / 2)
        Config.setStringRollRange((AERollPoint - GDRollPoint) / 3)
        prefRepo.setRollCentre(Config.rollCentre)
        prefRepo.setStringRollRange(Config.stringRollRange)
    }

    fun resetRollPoints () {
        AERollPoint = 45f
        GDRollPoint = -45f
        Config.setRollCentre((GDRollPoint + AERollPoint) / 2)
        Config.setStringRollRange((AERollPoint - GDRollPoint) / 3)
        prefRepo.setRollCentre(Config.rollCentre)
        prefRepo.setStringRollRange(Config.stringRollRange)
    }

    fun setTiltAwayLimit () {
        tiltAwayPitch = currentPitch
        Config.setPitchCentre((tiltAwayPitch + tiltTowardPitch) / 2)
        Config.setTotalPitchRange(tiltTowardPitch - tiltAwayPitch)
        prefRepo.setPitchCentre(Config.pitchCentre)
        prefRepo.setTotalPitchRange(Config.totalPitchRange)
    }

    fun setTiltTowardLimit () {
        tiltTowardPitch = currentPitch
        Config.setPitchCentre((tiltAwayPitch + tiltTowardPitch) / 2)
        Config.setTotalPitchRange(tiltTowardPitch - tiltAwayPitch)
        prefRepo.setPitchCentre(Config.pitchCentre)
        prefRepo.setTotalPitchRange(Config.totalPitchRange)
    }

    fun resetTiltLimits () {
        tiltAwayPitch = 0f
        tiltTowardPitch = -90f
        Config.setPitchCentre((tiltAwayPitch + tiltTowardPitch) / 2)
        Config.setTotalPitchRange(tiltTowardPitch - tiltAwayPitch)
        prefRepo.setPitchCentre(Config.pitchCentre)
        prefRepo.setTotalPitchRange(Config.totalPitchRange)
    }
}