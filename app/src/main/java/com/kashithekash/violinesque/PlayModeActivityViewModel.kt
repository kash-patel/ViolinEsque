package com.kashithekash.violinesque

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PlayModeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val rotationReader : RotationReader = RotationReader(context = application)
    private val stringManager : StringManager = StringManager()
    private val soundManager : SoundManager = SoundManager(context = application)

    private var currentString: MutableLiveData<ViolinString> = MutableLiveData<ViolinString>(ViolinString.A)

    private val config: Config = Config
    private val prefRepo: PrefRepo = PrefRepo(application.applicationContext)

    fun buttonTouched (currentString: ViolinString, buttonNumber: Int) {
        soundManager.handleButtonTouched(currentString, buttonNumber)
    }

    fun buttonReleased (currentString: ViolinString, buttonNumber: Int) {
        soundManager.handleButtonReleased(currentString, buttonNumber)
    }

    fun stringChanged (newString: ViolinString) {
        soundManager.handleStringChange(newString)
    }

    fun updateCurrentString (calibratedRoll : Float) {
        currentString.value = stringManager.calculateCurrentString(calibratedRoll)
    }

    fun getCurrentStringLiveData () : LiveData<ViolinString> {
        return currentString
    }

    fun getConfigStateLiveData () : LiveData<Long> {
        return config.configState
    }
    fun getButtonInteractabilityArray () : Array<Interactability> {
        return config.buttonInteractabilities
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

        config.init(savedButtonInteractabilities)
    }
}