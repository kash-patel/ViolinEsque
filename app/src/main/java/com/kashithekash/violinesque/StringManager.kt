package com.kashithekash.violinesque

enum class ViolinString {
    G, D, A, E
}

class StringManager () {

    private val config: Config = Config

    fun calculateCurrentString(calibratedRoll : Float) : ViolinString {

        var currentString: ViolinString = ViolinString.A

        if (calibratedRoll <= config.rollCentre - config.stringRollRange) {
            currentString = ViolinString.G
        } else if (calibratedRoll > config.rollCentre - config.stringRollRange && calibratedRoll <= config.rollCentre) {
            currentString = ViolinString.D
        } else if (calibratedRoll > config.rollCentre && calibratedRoll <= config.rollCentre + config.stringRollRange) {
            currentString = ViolinString.A
        } else if (calibratedRoll > config.rollCentre + config.stringRollRange) {
            currentString = ViolinString.E
        }

        return currentString
    }
}