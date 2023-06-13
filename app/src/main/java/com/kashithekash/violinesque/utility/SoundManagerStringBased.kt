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
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

private const val SoundUpdateDelayMs = 10
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

private var fadeInDuration: Int = 100
private var blendDuration: Int = 100
private var fadeOutDuration: Int = 200
private var fadeOutDelay: Int = 100

private val isFingerPressed: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false, false)
private var currentHandPosition: Int = 1
private var activeString: ViolinString? = null
private var highestPressedFinger: Int = -1
private var activeStreamRate: Float = 1f
private var silenceStreamID: Int = 0

private var bodyVibrationContributions: Array<Float> = arrayOf(0f, 0f, 0f, 0f)  // For harmonics

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

class SoundManagerStringBased (context: Context) {

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

        activeString = newString
    }

    fun handleHandPositionChange (newHandPosition: Int) {

        currentHandPosition = newHandPosition
    }

    private suspend fun manageString (string: ViolinString) {

        var time: Long = 0
        var fadeInStartTime: Long = 0
        var fadeOutStartTime: Long = 0
        var cachedHighestPressedFinger: Int = -1
        var cachedHandPosition: Int = 1
        var activeStreamID: Int = 0
        var fadingStreamID: Int = 0
        var activeStreamVolume: Float = 0f
        var fadingStreamVolume: Float = 0f


        while (true) {

            if (activeString == null) continue

            // Starting streams
            if (activeString == string) {

                // Finger or Hand Position change
                if  (cachedHighestPressedFinger != highestPressedFinger || (cachedHandPosition != currentHandPosition && highestPressedFinger > 0)) {

                    cachedHighestPressedFinger = highestPressedFinger
                    cachedHandPosition = currentHandPosition

                    // Shift streams if something is already playing
                    if (activeStreamID > 0) {
                        if (fadingStreamID > 0) stop(fadingStreamID)
                        fadingStreamID = activeStreamID
                        fadingStreamVolume = activeStreamVolume
                        fadeOutStartTime = time + fadeOutDelay
                    }

                    // Start new stream at current string and position
                    activeStreamID = if (cachedHighestPressedFinger == -1) 0 else play(string, calculateNoteMapIndex(cachedHandPosition, cachedHighestPressedFinger))
                    activeStreamVolume = 0f
                    fadeInStartTime = time
                }
            }

            // Fading out and stopping streams
            else {

                // Reset position
                cachedHighestPressedFinger = -1

                // If something is playing and string changes, fade it out
                if (activeStreamID > 0) {
                    stop(fadingStreamID)
                    fadingStreamID = activeStreamID
                    fadingStreamVolume = activeStreamVolume
                    fadeOutStartTime = time + fadeOutDelay
                    activeStreamID = 0
                    activeStreamVolume = 0f
                }
            }

            // Increase active stream volume
            if (activeStreamID > 0 && activeStreamVolume < 1f - VolumeThreshold) {

                // Linear fade
//                activeStreamVolume += if (fadingStreamID > 0)
//                    (MAX_VOLUME - INITIAL_VOLUME) * SOUND_UPDATE_DELAY_MS / blendTime
//                else (MAX_VOLUME - INITIAL_VOLUME) * SOUND_UPDATE_DELAY_MS / fadeInTime

                // Experimental logarithmic fade-in
//                activeStreamVolume = if (fadingStreamID > 0)
//                    -exp(LnVolumeThreshold / blendDuration).pow((time - fadeInStartTime).toInt()) + 1f
//                else
//                    -exp(LnVolumeThreshold / fadeInDuration).pow((time - fadeInStartTime).toInt()) + 1f

                // Experimental quadratic fade-in
                activeStreamVolume = if (fadingStreamID > 0)
                    sqrt((time - fadeInStartTime).toFloat() / blendDuration).coerceIn(0f, 1f)
                else
                    sqrt((time - fadeInStartTime).toFloat() / fadeOutDuration).coerceIn(0f, 1f)

                setVolume(activeStreamID, min(1f, activeStreamVolume))
            }

            // Decrease fading stream volume
            if (fadingStreamID > 0 && fadingStreamVolume > 0 + VolumeThreshold && time >= fadeOutStartTime) {

                // Linear fade-out
//                fadingStreamVolume -= if (cachedHighestPressedFinger > -1 || activeStreamID > 0)
//                    MAX_VOLUME * SOUND_UPDATE_DELAY_MS / blendDuration
//                else MAX_VOLUME * SOUND_UPDATE_DELAY_MS / fadeOutDuration

                // Experimental logarithmic fade-out
                fadingStreamVolume = if (cachedHighestPressedFinger > -1 || activeStreamID > 0)
                    exp(LnVolumeThreshold / blendDuration).pow((time - fadeOutStartTime).toInt())
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

            bodyVibrationContributions[string.ordinal] = max(fadingStreamVolume, activeStreamVolume)

            time += SoundUpdateDelayMs
            delay(SoundUpdateDelayMs.milliseconds)
        }
    }

    suspend fun manageGString () {

        withContext(DefaultDispatcher) {
            launch {
                manageString(ViolinString.G)
            }
        }
    }

    suspend fun manageDString () {

        withContext(DefaultDispatcher) {
            launch {
                manageString(ViolinString.D)
            }
        }
    }

    suspend fun manageAString () {

        withContext(DefaultDispatcher) {
            launch {
                manageString(ViolinString.A)
            }
        }
    }

    suspend fun manageEString () {

        withContext(DefaultDispatcher) {
            launch {
                manageString(ViolinString.E)
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

        activeStreamRate = 2.0.pow((position % 12) / 12.0).toFloat()

        val noteMapKey: Int = string.ordinal * 10 + position / 12
        val streamID = soundPool.play(
            noteSoundFileHashMap[noteMapKey]!!,
            volume,
            volume,
            0,
            -1,
            activeStreamRate
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