package com.example.violinesque

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.PI

private const val TAG : String = "Orientation"
private const val RAD_TO_DEG : Float = 180 / PI.toFloat()

