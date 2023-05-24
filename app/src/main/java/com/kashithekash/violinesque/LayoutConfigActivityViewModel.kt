package com.kashithekash.violinesque

import android.app.Application
import androidx.lifecycle.AndroidViewModel

enum class Interactability {
    ENABLED,
    DISABLED,
    HIDDEN
}

class LayoutConfigActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val prefRepo : PrefRepo = PrefRepo(application)
    private val config: Config = Config

    fun setButtonInteractability (buttonNumber: Int, newInteractability: Interactability) {
        config.buttonInteractabilities[buttonNumber] = newInteractability
        config.configState.postValue(System.currentTimeMillis())
        prefRepo.setButtonInteractability(buttonNumber, newInteractability)
    }

    fun getButtonInteractability (buttonNumber: Int) : Interactability {
        return prefRepo.getButtonInteractability(buttonNumber)
    }
}