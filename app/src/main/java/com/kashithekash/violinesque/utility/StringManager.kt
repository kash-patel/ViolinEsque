package com.kashithekash.violinesque.utility

class StringManager () {

    fun calculateCurrentString(roll: Float) : ViolinString {

        var currentString: ViolinString = ViolinString.A

        if (roll <= Config.rollCentre - Config.stringRollRange) {
            currentString = if (Config.invertRoll) ViolinString.E else ViolinString.G
        } else if (roll > Config.rollCentre - Config.stringRollRange && roll <= Config.rollCentre) {
            currentString = if (Config.invertRoll) ViolinString.A else ViolinString.D
        } else if (roll > Config.rollCentre && roll <= Config.rollCentre + Config.stringRollRange) {
            currentString = if (Config.invertRoll) ViolinString.D else ViolinString.A
        } else if (roll > Config.rollCentre + Config.stringRollRange) {
            currentString = if (Config.invertRoll) ViolinString.G else ViolinString.E
        }

        return currentString
    }
}