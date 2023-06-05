package com.kashithekash.violinesque.utility

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PrefRepo (context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("com.kashithekash.violinesque.prefs", Context.MODE_PRIVATE)

    private val editor = sharedPrefs.edit()

    fun setExpandButtons (newExpandButtons: Boolean) {
        editor.putBoolean("exapnd_buttons", newExpandButtons)
        editor.apply()
    }

    fun setInvertPitch (newInvertPitch: Boolean) {
        editor.putBoolean("invert_pitch", newInvertPitch)
        editor.apply()
    }

    fun setInvertRoll (newInvertRoll: Boolean) {
        editor.putBoolean("invert_roll", newInvertRoll)
        editor.apply()
    }

    fun setRollCentre (rollCentre: Float) {
        editor.putFloat("roll_centre", rollCentre)
        editor.apply()
    }

    fun setStringRollRange (stringRollRange: Float) {
        editor.putFloat("string_roll_range", stringRollRange)
        editor.apply()
    }

    fun setPitchCentre (pitchCentre: Float) {
        editor.putFloat("pitch_centre", pitchCentre)
        editor.apply()
    }

    fun setTotalPitchRange (positionPitchRange: Float) {
        editor.putFloat("total_pitch_range", positionPitchRange)
        editor.apply()
    }

    fun setHandPositionsList (newhandPositions: List<Int>) {
        val gson: Gson = Gson()
        val json = gson.toJson(newhandPositions)
        editor.putString("hand_positions_list", json)
        editor.apply()
    }

    fun getRollCentre () : Float {
        return sharedPrefs.getFloat("roll_centre", 0f)
    }

    fun getStringRollRange () : Float {
        return sharedPrefs.getFloat("string_roll_range", 20f)
    }

    fun getPitchCentre () : Float {
        return sharedPrefs.getFloat("pitch_centre", -45f)
    }

    fun getTotalPitchRange () : Float {
        return sharedPrefs.getFloat("total_pitch_range", -90f)
    }

    fun getExpandButtons () : Boolean {
        return sharedPrefs.getBoolean("expand_buttons", false)
    }

    fun getInvertPitch () : Boolean {
        return sharedPrefs.getBoolean("invert_pitch", false)
    }

    fun getInvertRoll () : Boolean {
        return sharedPrefs.getBoolean("invert_roll", false)
    }

    fun getHandPositionsList () : List<Int> {

        var handPositionsList: List<Int> = listOf(1, 3)
        val serializedObject = sharedPrefs.getString("hand_positions_list", null)

        if (serializedObject != null) {

            val gson: Gson = Gson()
            handPositionsList = gson.fromJson(serializedObject, Array<Int>::class.java).toList()
        }

        return handPositionsList
    }
}