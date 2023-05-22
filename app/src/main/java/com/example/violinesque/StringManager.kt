package com.example.violinesque

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

enum class ViolinString {
    G, D, A, E
}

class StringManager () {

    private var stringTiltRange: Int = 30  // In degrees


    fun calculateCurrentString(calibratedRoll : Float) : ViolinString {

        var currentString: ViolinString = ViolinString.A

        if (calibratedRoll <= -stringTiltRange) {
            currentString = ViolinString.G
        } else if (calibratedRoll > -stringTiltRange && calibratedRoll <= 0) {
            currentString = ViolinString.D
        } else if (calibratedRoll > 0 && calibratedRoll <= stringTiltRange) {
            currentString = ViolinString.A
        } else if (calibratedRoll > stringTiltRange) {
            currentString = ViolinString.E
        }

        return currentString
    }
}