package com.kashithekash.violinesque

import androidx.lifecycle.MutableLiveData

object Config {

    var rollCentre: Float = 0f
        private set
    var stringRollRange: Float = 20f
        private set

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

    fun init (savedButtonInteractabilities: Array<Interactability>, savedRollCentre: Float, savedStringRollRange: Float) {

        for (i in 0..12) {
            buttonInteractabilities[i] = savedButtonInteractabilities[i]
        }

        configState.value = System.currentTimeMillis()
        rollCentre = savedRollCentre
        stringRollRange = savedStringRollRange
    }

    fun setRollCentre (rollCentre: Float) {
        this.rollCentre = rollCentre
    }

    fun setStringRollRange (stringRollRange: Float) {
        this.stringRollRange = stringRollRange
    }
}