package com.kashithekash.violinesque

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CalibrationSettingsActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val rotationReader : RotationReader = RotationReader(context = application)
    private val stringManager : StringManager = StringManager()
    private val prefRepo : PrefRepo = PrefRepo(application)
    private val config: Config = Config

    private var ccwLimit: Float = -30f
    private var cwLimit: Float = 30f
    private var currentRoll: Float = 0f

    private var currentString: MutableLiveData<ViolinString> = MutableLiveData<ViolinString>(ViolinString.A)

    fun calibrateCCWLimit () {
        ccwLimit = currentRoll
        config.setRollCentre((ccwLimit + cwLimit) / 2)
        config.setStringRollRange((cwLimit - ccwLimit) / 3)
        prefRepo.setRollCentre(config.rollCentre)
        prefRepo.setStringRollRange(config.stringRollRange)
    }

    fun calibrateCWLimit () {
        cwLimit = currentRoll
        config.setRollCentre((ccwLimit + cwLimit) / 2)
        config.setStringRollRange((cwLimit - ccwLimit) / 3)
        prefRepo.setRollCentre(config.rollCentre)
        prefRepo.setStringRollRange(config.stringRollRange)
    }

    fun resetCalibration () {

        config.setRollCentre(0f)
        config.setStringRollRange(20f)
        prefRepo.setRollCentre(0f)
        prefRepo.setStringRollRange(20f)
    }

    fun getCurrentStringLiveData () : LiveData<ViolinString> {
        return currentString
    }

    fun getRotationVector () = rotationReader

    fun updateRoll (roll: Float) {
        currentRoll = roll
        currentString.value = stringManager.calculateCurrentString(roll)
    }
}