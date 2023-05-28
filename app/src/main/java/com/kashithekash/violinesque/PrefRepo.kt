package com.kashithekash.violinesque

import android.content.Context
import android.content.SharedPreferences

class PrefRepo (context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("com.kashithekash.violinesque.prefs", Context.MODE_PRIVATE)

    private val editor = sharedPrefs.edit()

    fun setButtonInteractability(buttonNumber: Int, newInteractability: Interactability) {
        editor.putInt("button_${buttonNumber}_Interactability", newInteractability.ordinal)
        editor.apply()
    }

    fun getButtonInteractability(buttonNumber: Int): Interactability {
        return Interactability.values()[sharedPrefs.getInt(
            "button_${buttonNumber}_Interactability",
            Interactability.ENABLED.ordinal
        )]
    }

    fun setRollCentre (rollCentre: Float) {
        editor.putFloat("roll_centre", rollCentre)
        editor.apply()
    }

    fun setStringRollRange (stringRollRange: Float) {
        editor.putFloat("string_roll_range", stringRollRange)
        editor.apply()
    }

    fun getRollCentre () : Float {
        return sharedPrefs.getFloat("roll_centre", 0f)
    }

    fun getStringRollRange () : Float {
        return sharedPrefs.getFloat("string_roll_range", 20f)
    }
}