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

private const val TAG : String = "Orientation"
private const val RAD_TO_DEG : Float = 180 / PI.toFloat()

class RotationReader(context: Context) : LiveData<FloatArray>(), SensorEventListener {

    private val sensorManager : SensorManager = context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
    private var rotationSensor : Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private var lastAccuracy : Int = SensorManager.SENSOR_STATUS_UNRELIABLE
    private var rv : FloatArray = floatArrayOf(0f, 0f, 0f)

    private val azimuthAxis: Array<Float> = arrayOf(1f, 0f, 0f)
    private val pitchAxis: Array<Float> = arrayOf(0f, 1f, 0f)
    private val rollAxis: Array<Float> = arrayOf(0f, 0f, 1f)

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
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
            worldAxisForDeviceAxisY, adjustedRotationMatrix)

        // Adjusted Rotation Matrix Rotation Vectors
        // Pitch    (Device X): 0, 3, 7
        // Roll     (Device Y): 1, 4, 7
        // Azimuth  (Device Z): 2, 5, 8

//        Log.w("RotationReader", "${adjustedRotationMatrix[0]}, ${adjustedRotationMatrix[3]}, ${adjustedRotationMatrix[6]}")

        // Transform rotation matrix into azimuth/pitch/roll
        val orientation = FloatArray(3)
        SensorManager.getOrientation(adjustedRotationMatrix, orientation)

        // Convert radians to degrees
        // orientation[1] // Pitch is used to calculate...
        // orientation[0] // Yaw, which is used with pitch to calculate...
        // orientation[2] // Roll.

        /*

        // Calibrated pitch axis
        adjustedRotationMatrix[0] = cos(orientation[2])
        adjustedRotationMatrix[3] = sin(orientation[0])
        adjustedRotationMatrix[6] = sin(orientation[2])

        // Calibrated yaw axis
        adjustedRotationMatrix[2] = sin(orientation[2])
        adjustedRotationMatrix[5] = sin(orientation[1])
        adjustedRotationMatrix[8] = cos(orientation[1])

        // Calibrated roll axis
        adjustedRotationMatrix[1] = sin(orientation[0])
        adjustedRotationMatrix[4] = cos(orientation[1])
        adjustedRotationMatrix[7] = sin(orientation[1])

        SensorManager.getOrientation(adjustedRotationMatrix, orientation)

        */

        // SensorManager.getAngleChange!!!

        rv[0] = orientation[0] // Azimuth
        rv[1] = orientation[1] // Pitch
        rv[2] = orientation[2] // Roll

        value = rv
    }
}