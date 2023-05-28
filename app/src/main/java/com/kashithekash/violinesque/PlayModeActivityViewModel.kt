package com.kashithekash.violinesque

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlayModeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val rotationReader : RotationReader = RotationReader(context = application)
    private val stringManager : StringManager = StringManager()
    private val soundManager : SoundManager = SoundManager(context = application)

    private var currentString: MutableLiveData<ViolinString> = MutableLiveData<ViolinString>(ViolinString.A)

    private val config: Config = Config
    private val prefRepo: PrefRepo = PrefRepo(application.applicationContext)

    fun buttonTouched (buttonNumber: Int) {
        soundManager.handleButtonTouch(buttonNumber)
    }

    fun buttonReleased (buttonNumber: Int) {
        soundManager.handleButtonRelease(buttonNumber)
    }

    fun stringChanged (newString: ViolinString) {
        soundManager.handleStringChange(newString)
    }

    fun monitorStrings () {
        viewModelScope.launch { soundManager.manageActiveStream() }
        viewModelScope.launch { soundManager.manageFadingPositionStream() }
        viewModelScope.launch { soundManager.manageFadingStringStream() }
        viewModelScope.launch { soundManager.manageTerminalStreams() }
    }

    fun updateRoll (roll : Float) {
        currentString.value = stringManager.calculateCurrentString(roll)
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

        config.init(savedButtonInteractabilities, prefRepo.getRollCentre(), prefRepo.getStringRollRange())
    }

    fun releaseResources () {
        soundManager.release()
    }
}