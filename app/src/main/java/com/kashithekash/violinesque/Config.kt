package com.kashithekash.violinesque

import androidx.lifecycle.MutableLiveData

object Config {

    val buttonInteractabilities : Array<Interactability> =
        arrayOf(
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED,
            Interactability.ENABLED
        )

    val configState: MutableLiveData<Long> = MutableLiveData(System.currentTimeMillis())

    fun init (savedButtonInteractabilities: Array<Interactability>) {

        for (i in 0..12) {
            buttonInteractabilities[i] = savedButtonInteractabilities[i]
        }

        configState.value = System.currentTimeMillis()
    }
}