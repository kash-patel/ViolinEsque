package com.kashithekash.violinesque.utility

object Config {

    var rollCentre: Float = 0f
        private set
    var stringRollRange: Float = 45f
        private set
    var pitchCentre: Float = -22.55f
        private set
    var totalPitchRange: Float = -45f
        private set
    var expandButtons: Boolean = false
        private set
    var invertPitch: Boolean = false
        private set
    var invertRoll: Boolean = false
        private set
    var handPositionsList: List<Int> = listOf(1, 3)
        private set

    fun init (
        savedRollCentre: Float,
        savedStringRollRange: Float,
        savedPitchCentre: Float,
        savedTotalPitchRange: Float,
        savedExpandButtons: Boolean,
        savedInvertRoll: Boolean,
        savedInvertPitch: Boolean,
        savedHandPositionsList: List<Int>
    ) {

        rollCentre = savedRollCentre
        stringRollRange = savedStringRollRange
        pitchCentre = savedPitchCentre
        totalPitchRange = savedTotalPitchRange
        expandButtons = savedExpandButtons
        invertRoll = savedInvertRoll
        invertPitch = savedInvertPitch
        handPositionsList = savedHandPositionsList
    }

    fun setRollCentre (newRollCentre: Float) {
        rollCentre = newRollCentre
    }

    fun setStringRollRange (newStringRollRange: Float) {
        stringRollRange = newStringRollRange
    }

    fun setPitchCentre (newPitchCentre: Float) {
        pitchCentre = newPitchCentre
    }

    fun setTotalPitchRange (newTotalPitchRange: Float) {
        totalPitchRange = newTotalPitchRange
    }

    fun setExpandButtons (newExpandButtons: Boolean) {
        expandButtons = newExpandButtons
    }

    fun setInvertRoll (newInvertRoll: Boolean) {
        invertRoll = newInvertRoll
    }

    fun setInvertPitch (newInvertPitch: Boolean) {
        invertPitch = newInvertPitch
    }

    fun setHandPositionsList (newHandPositionsList: List<Int>) {
        handPositionsList = newHandPositionsList
    }
}