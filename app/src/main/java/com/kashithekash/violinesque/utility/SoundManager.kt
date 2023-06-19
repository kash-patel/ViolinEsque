package com.kashithekash.violinesque.utility

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import com.kashithekash.violinesque.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

private const val SoundUpdateDelayMs: Int = 10  // In ms
private const val ButtonPressCommitmentThreshold: Int = 10  // In ms
private const val VibratoRate: Int = 8    // In Hertz
private const val VibratoPitchAmplitude: Float = 0.25f
private val VibratoRateWaveAmplitude: Float = 2.0.pow(VibratoPitchAmplitude / 12.0).toFloat()
private const val VolumeThreshold: Float = 0.01f
private val LnVolumeThreshold: Float = ln(VolumeThreshold)
private val DefaultDispatcher: CoroutineDispatcher = Dispatchers.Default

//private var fadeInDuration: Int = Config.fadeInTime
//private var blendDuration: Int = Config.blendTime
//private var fadeOutDuration: Int = Config.fadeOutTime
//private var fadeOutDelay: Int = Config.fadeOutDelay

private const val StreamLoadWaitTime: Int = 20
private var fadeInDuration: Int = 50
private var blendDuration: Int = 80
private var fadeOutDuration: Int = 250
private var fadeOutDelay: Int = 100

private val isFingerPressed: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false, false)
private var currentHandPosition: Int = 1
private var selectedString: ViolinString? = null
private var highestPressedFinger: Int = -1
private var finalActiveStreamRate: Float = 1f
private var initialActiveStreamRate: Float = 1f
private var silenceStreamID: Int = 0

private val soundPool : SoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

    val audioAttributes : AudioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_GAME)
        .build()

    SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(audioAttributes)
        .build()

} else {
    SoundPool(4, AudioManager.USE_DEFAULT_STREAM_TYPE, 0)
}

private val noteSoundFileHashMap : HashMap<Int, Int> = HashMap(1)

class SoundManager (context: Context) {

    init {
        loadSoundFiles(context)
//        playSilence()
    }

    fun handleButtonTouch (position: Int) {

        isFingerPressed[position] = true

        if (position < highestPressedFinger) return

        highestPressedFinger = position
    }

    fun handleButtonRelease (position: Int) {

        isFingerPressed[position] = false

        if (position != highestPressedFinger) return

        highestPressedFinger = getHighestPressedFinger()
    }

    fun handleStringChange (newString: ViolinString) {

        selectedString = newString
    }

    fun handleHandPositionChange (newHandPosition: Int) {

        currentHandPosition = newHandPosition
    }

    /**
     * This SoundManager implements an approximation of whole-body vibration rather than individual string vibration;
     * as such it should be more accurate to the real thing.
     */
    private suspend fun manageVibration () {

        var time: Long = 0L
        var fadeInStartTime: Long = 0L
        var fadeOutStartTime: Long = 0L
        var cachedHighestPressedFinger: Int = -1
        var cachedHandPosition: Int = 1
        var activeString: ViolinString? = null
        var activeStreamID: Int = 0
        var fadingStreamID: Int = 0
        var activeStreamVolume: Float = 0f
        var initialFadingStreamVolume: Float = 0f
        var fadingStreamVolume: Float = 0f
        var activeStreamRate: Float = 1f

        while (true) {

            if (selectedString == null) continue
//
            if ((cachedHandPosition != currentHandPosition && cachedHighestPressedFinger > 0) || cachedHighestPressedFinger != highestPressedFinger || selectedString != activeString) {

                cachedHandPosition = currentHandPosition
                activeString = selectedString
                cachedHighestPressedFinger = highestPressedFinger

                // Shift streams if something is already playing
                if (activeStreamID > 0 && time - (fadeInStartTime + StreamLoadWaitTime) >= ButtonPressCommitmentThreshold) {
                    if (fadingStreamID > 0) stop(fadingStreamID)
                    fadingStreamID = activeStreamID
                    fadingStreamVolume = activeStreamVolume
                    initialFadingStreamVolume = fadingStreamVolume
                    fadeOutStartTime = time + fadeOutDelay
                }

                // Start new stream at current string and position if button pressed
                activeStreamID = if (cachedHighestPressedFinger == -1) 0 else play(activeString!!, calculateNoteMapIndex(cachedHandPosition, cachedHighestPressedFinger))
                activeStreamVolume = 0f
                fadeInStartTime = time
            }

            // Increase active stream volume
            if (activeStreamID > 0 && activeStreamVolume < 1f - VolumeThreshold && time >= fadeInStartTime + StreamLoadWaitTime) {

                // Linear fade
//                activeStreamVolume += if (fadingStreamID > 0)
//                    SoundUpdateDelayMs.toFloat() / blendDuration
//                else SoundUpdateDelayMs.toFloat() / fadeInDuration

                // Experimental logarithmic fade-in
                activeStreamVolume = if (fadingStreamID > 0)
                    -exp(LnVolumeThreshold / blendDuration).pow((time - fadeInStartTime).toInt()) + 1f
                else
                    -exp(LnVolumeThreshold / fadeInDuration).pow((time - fadeInStartTime).toInt()) + 1f

                // Experimental quadratic fade-in
//                activeStreamVolume = if (fadingStreamID > 0)
//                    sqrt((time - fadeInStartTime).toFloat() / blendDuration).coerceIn(0f, 1f)
//                else
//                    sqrt((time - fadeInStartTime).toFloat() / fadeOutDuration).coerceIn(0f, 1f)

                setVolume(activeStreamID, min(1f, activeStreamVolume))

                // Increase pitch from flat to base
                activeStreamRate = (finalActiveStreamRate - initialActiveStreamRate) * if (fadingStreamID > 0)
                    -exp(LnVolumeThreshold / blendDuration).pow((time - fadeInStartTime).toInt())
                else
                    -exp(LnVolumeThreshold / fadeInDuration).pow((time - fadeInStartTime).toInt())

                activeStreamRate += finalActiveStreamRate

                setRate(activeStreamID, activeStreamRate)
            }

            // Decrease fading stream volume
            if (fadingStreamID > 0 && fadingStreamVolume > VolumeThreshold && time >= fadeOutStartTime) {

                // Linear fade-out
//                fadingStreamVolume -= if (cachedHighestPressedFinger > -1 || activeStreamID > 0)
//                    SoundUpdateDelayMs.toFloat() / blendDuration
//                else SoundUpdateDelayMs.toFloat() / fadeOutDuration

                // Experimental logarithmic fade-out
                fadingStreamVolume = initialFadingStreamVolume * if (cachedHighestPressedFinger > -1 || activeStreamID > 0)
                    0.5f * exp(LnVolumeThreshold / blendDuration).pow((time - fadeOutStartTime).toInt())
                else
                    exp(LnVolumeThreshold / fadeOutDuration).pow((time - fadeOutStartTime).toInt())

                // Experimental quadratic fade-out
//                fadingStreamVolume = if (cachedHighestPressedFinger > -1 || activeStreamID > 0)
//                    sqrt((fadeOutStartTime - time).toFloat() / blendDuration + 1)
//                else
//                    sqrt((fadeOutStartTime - time).toFloat() / fadeOutDuration + 1)

                // Experimental quadratic fade-out 2
//                fadingStreamVolume = if (cachedHighestPressedFinger > -1 || activeStreamID > 0)
//                    ((time - fadeOutStartTime).toFloat() / blendDuration - 1).pow(2).coerceIn(0f, MAX_VOLUME)
//                else
//                    ((time - fadeOutStartTime).toFloat() / fadeOutDuration - 1).pow(2).coerceIn(0f, MAX_VOLUME)

                setVolume(fadingStreamID, max(0f, fadingStreamVolume))
            }

            else if (fadingStreamID > 0 && fadingStreamVolume <= 0 + VolumeThreshold) {
                stop(fadingStreamID)
                fadingStreamID = 0
                fadingStreamVolume = 0f
            }

            // Vibrato
//            if (activeStreamID > 0 && activeStreamVolume >= 0.8f) {
//                setRate(
//                    activeStreamID,
//                    activeStreamRate * (2 + (1 - VibratoRateWaveAmplitude) * cos(VibratoRate * time.toDouble()).toFloat() - VibratoRateWaveAmplitude)
//                )
//            }

//                    bodyVibrationContributions[activeString!!.ordinal] = max(fadingStreamVolume, activeStreamVolume)

            time += SoundUpdateDelayMs

            delay(SoundUpdateDelayMs.milliseconds)
        }
    }

    suspend fun start () {
        withContext(DefaultDispatcher) {
            launch {
                manageVibration()
            }
        }
    }

    fun setFadeInTime (newFadeInTime: Int) {
        fadeInDuration = newFadeInTime
    }

    fun setBlendTime (newBlendTime: Int) {
        blendDuration = newBlendTime
    }

    fun setFadeOutTime (newFadeOutTime: Int) {
        fadeOutDuration = newFadeOutTime
    }

    fun setFadeOutDelay (newFadeOutDelay: Int) {
        fadeOutDelay = newFadeOutDelay
    }

    private fun play (string: ViolinString, position: Int, volume: Float = 0f, rate: Float = 1f) : Int {

        /*
        Trying out dynamically produced sound centred around A4 (440 Hz).

        Each semitone is a 100 cent interval, and since an octave has 12 semitones,
        there are 1200 cents per octave, so 100 cents = 1 / 12 octaves.

        So, a note n semitones from A4 has the rate 2^(n / 12).
        */

        finalActiveStreamRate = 2.0.pow((position % 12) / 12.0).toFloat()
        initialActiveStreamRate = 2.0.pow(-1 / 12.0).toFloat() * finalActiveStreamRate

        val noteMapKey: Int = string.ordinal * 10 + position / 12
        val streamID = soundPool.play(
            noteSoundFileHashMap[noteMapKey]!!,
            volume,
            volume,
            0,
            -1,
            initialActiveStreamRate
        )

        Log.w("SoundManager", "Started $streamID")

        return streamID
    }

    private fun stop (streamID: Int) {
        soundPool.stop(streamID)
        Log.w("SoundManager", "Stopped $streamID.")
    }

    private fun setVolume (streamID: Int, newVolume: Float) {
        soundPool.setVolume(streamID, newVolume, newVolume)
    }

    private fun setRate (streamID: Int, newRate: Float) {
        soundPool.setRate(streamID, newRate)
    }

    private fun getHighestPressedFinger () : Int {

        var maxIndex: Int = -1

        for (i in isFingerPressed.indices)
            if (isFingerPressed[i]) maxIndex = i

        return maxIndex
    }

    private fun calculateNoteMapIndex(handPosition: Int, fingerPosition: Int) : Int {
        return if (fingerPosition == 0) 0
        else handPostionStartIndices[handPosition] + fingerPosition
    }

    private fun playSilence () {
        silenceStreamID = soundPool.play(noteSoundFileHashMap[0]!!, 0f, 0f, -1, -1, 1f)
    }

    private fun stopSilence () {
        stop(silenceStreamID)
    }

    private fun loadSoundFiles (context: Context) {

        noteSoundFileHashMap[ViolinString.G.ordinal * 10] = soundPool.load(context, R.raw.g_g3, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 10 + 1] = soundPool.load(context, R.raw.g_g4, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 10 + 2] = soundPool.load(context, R.raw.g_g5, 0)

        noteSoundFileHashMap[ViolinString.D.ordinal * 10] = soundPool.load(context, R.raw.d_d4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 10 + 1] = soundPool.load(context, R.raw.d_d5, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 10 + 2] = soundPool.load(context, R.raw.d_d6, 0)

        noteSoundFileHashMap[ViolinString.A.ordinal * 10] = soundPool.load(context, R.raw.a_a4, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 10 + 1] = soundPool.load(context, R.raw.a_a5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 10 + 2] = soundPool.load(context, R.raw.a_a6, 0)

        noteSoundFileHashMap[ViolinString.E.ordinal * 10] = soundPool.load(context, R.raw.e_e5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 10 + 1] = soundPool.load(context, R.raw.e_e6, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 10 + 2] = soundPool.load(context, R.raw.e_e7, 0)
    }

    fun release () {
//        stopSilence()
        soundPool.release()
    }
}