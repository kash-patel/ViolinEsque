package com.kashithekash.violinesque.utility

object Config {

    var fadeInTime: Int = 100
        private set
    var blendTime: Int = 100
        private set
    var fadeOutTime: Int = 100
        private set
    var fadeOutDelay: Int = 100
        private set
    var rollCentre: Float = 0f
        private set
    var stringRollRange: Float = Pi / 3
        private set
    var pitchCentre: Float = -Pi / 8
        private set
    var totalPitchRange: Float = -Pi / 4
        private set
    var expandButtons: Boolean = false
        private set
    var alignButtonsToBottom: Boolean = false
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
        savedAlignButtonsToBottom: Boolean,
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
        alignButtonsToBottom = savedAlignButtonsToBottom
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

    fun setAlignButtonsToBottom (newAlignButtonsToBottom: Boolean) {
        alignButtonsToBottom = newAlignButtonsToBottom
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