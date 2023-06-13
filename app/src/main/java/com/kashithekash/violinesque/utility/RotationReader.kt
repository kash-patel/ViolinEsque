package com.kashithekash.violinesque.utility

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.LiveData
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class RotationReader(context: Context) : LiveData<FloatArray>(), SensorEventListener {

    private val sensorManager : SensorManager = context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
    private var rotationSensor : Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private var lastAccuracy : Int = SensorManager.SENSOR_STATUS_UNRELIABLE
    private var rv : FloatArray = floatArrayOf(0f, 0f, 0f)

    override fun onActive() {
        super.onActive()
        startListening()
    }

    override fun onInactive() {
        super.onInactive()
        stopListening()
    }

    private fun startListening () {
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    private fun stopListening () {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == rotationSensor) updateOrientation(event.values)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        lastAccuracy = accuracy
    }

    private fun updateOrientation(rotationVector : FloatArray) {

        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)

        val (worldAxisForDeviceAxisX, worldAxisForDeviceAxisY) = Pair(SensorManager.AXIS_X, SensorManager.AXIS_Y)

        val adjustedRotationMatrix = FloatArray(9)
        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            worldAxisForDeviceAxisX,
            worldAxisForDeviceAxisY,
            adjustedRotationMatrix
        )

        val orientation = FloatArray(3)
        SensorManager.getOrientation(adjustedRotationMatrix, orientation)

        rv[0] = orientation[0] // Azimuth
        rv[1] = orientation[1] // Pitch
        rv[2] = orientation[2] // Roll

        value = rv
    }
}