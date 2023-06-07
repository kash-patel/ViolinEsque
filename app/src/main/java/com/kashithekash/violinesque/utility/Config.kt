package com.kashithekash.violinesque.utility

object Config {

    var fadeInTime: Int = 10
        private set
    var blendTime: Int = 50
        private set
    var fadeOutTime: Int = 100
        private set
    var fadeOutDelay: Int = 0
        private set
    var rollCentre: Float = 0f
        private set
    var stringRollRange: Float = Pi / 4
        private set
    var pitchCentre: Float = -Pi.toFloat() / 8
        private set
    var totalPitchRange: Float = -Pi.toFloat() / 4
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
        savedFadeInTime: Int,
        savedBlendTime: Int,
        savedFadeOutTime: Int,
        savedFadeOutDelay: Int,
        savedRollCentre: Float,
        savedStringRollRange: Float,
        savedPitchCentre: Float,
        savedTotalPitchRange: Float,
        savedExpandButtons: Boolean,
        savedInvertRoll: Boolean,
        savedInvertPitch: Boolean,
        savedHandPositionsList: List<Int>
    ) {
        fadeInTime = savedFadeInTime
        blendTime = savedBlendTime
        fadeOutTime = savedFadeOutTime
        fadeOutDelay = savedFadeOutDelay
        rollCentre = savedRollCentre
        stringRollRange = savedStringRollRange
        pitchCentre = savedPitchCentre
        totalPitchRange = savedTotalPitchRange
        expandButtons = savedExpandButtons
        invertRoll = savedInvertRoll
        invertPitch = savedInvertPitch
        handPositionsList = savedHandPositionsList
    }

    fun setFadeInTime (newFadeInTime: Int) {
        fadeInTime = newFadeInTime
    }

    fun setBlendTime (newBlendTime: Int) {
        blendTime = newBlendTime
    }

    fun setFadeOutTime (newFadeOutTime: Int) {
        fadeOutTime = newFadeOutTime
    }

    fun setFadeOutDelay (newFadeOutDelay: Int) {
        fadeOutDelay = newFadeOutDelay
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