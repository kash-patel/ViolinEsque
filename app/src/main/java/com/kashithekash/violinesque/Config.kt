package com.kashithekash.violinesque

object Config {

    var rollCentre: Float = 0f
        private set
    var stringRollRange: Float = 20f
        private set

    val buttonInteractabilityArray : Array<Interactability> =
        arrayOf(
            Interactability.ENABLED, Interactability.ENABLED, Interactability.ENABLED,
            Interactability.ENABLED, Interactability.ENABLED, Interactability.ENABLED,
            Interactability.ENABLED, Interactability.ENABLED, Interactability.ENABLED,
            Interactability.ENABLED, Interactability.ENABLED, Interactability.ENABLED,
            Interactability.ENABLED
        )

    fun init (savedButtonInteractabilities: Array<Interactability>,
              savedRollCentre: Float,
              savedStringRollRange: Float
    ) {

        for (i in 0..12) {
            buttonInteractabilityArray[i] = savedButtonInteractabilities[i]
        }

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