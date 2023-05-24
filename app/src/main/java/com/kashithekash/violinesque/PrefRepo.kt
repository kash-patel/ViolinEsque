package com.kashithekash.violinesque

import android.content.Context
import android.content.SharedPreferences

class PrefRepo (private val context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("com.kashithekash.violinesque.prefs", Context.MODE_PRIVATE)

    private val editor = sharedPrefs.edit()

    fun setButtonInteractability(buttonNumber: Int, newInteractability: Interactability) {
        editor.putInt("button${buttonNumber}Interactability", newInteractability.ordinal)
        editor.apply()
    }

    fun getButtonInteractability(buttonNumber: Int): Interactability {
        return Interactability.values()[sharedPrefs.getInt(
            "button${buttonNumber}Interactability",
            Interactability.ENABLED.ordinal
        )]
    }
}