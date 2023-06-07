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
import java.util.Timer
import kotlin.concurrent.timerTask
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

private const val SOUND_UPDATE_DELAY_MS = 10
//private val HalfToneFrequencyMultiplier = 2.0.pow(1 / 12)
private const val MAX_VOLUME : Float = 1f
private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

private var fadeInTime: Int = Config.fadeInTime
private var blendTime: Int = Config.blendTime
private var fadeOutTime: Int = Config.fadeOutTime
private var fadeOutDelay: Int = Config.fadeOutDelay

private val isFingerPressed: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false, false)
private var currentHandPosition: Int = 1
private var activeString: ViolinString? = null
private var highestPressedFinger: Int = -1

private var isTimerRunning: Boolean = false

private val soundPool : SoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

    val audioAttributes : AudioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_GAME)
        .build()

    SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(audioAttributes)
        .build()

} else {
    SoundPool(3, AudioManager.USE_DEFAULT_STREAM_TYPE, 0)
}

private val noteSoundFileHashMap : HashMap<Int, Int> = HashMap(1)

class SoundManagerStringBased (context: Context) {

    init { loadSoundFiles(context) }

    fun handleButtonTouch (position: Int) {

        isFingerPressed[position] = true

        if (position < highestPressedFinger) return

        highestPressedFinger = position
    }

    fun handleButtonRelease (position: Int) {

        isFingerPressed[position] = false

        if (position != highestPressedFinger) return

        highestPressedFinger = getHighestPressedFinger()

        isTimerRunning = true
    }

    fun handleStringChange (newString: ViolinString) {

        activeString = newString
    }

    fun handleHandPositionChange (newHandPosition: Int) {

        currentHandPosition = newHandPosition
    }

    private suspend fun manageString (string: ViolinString) {

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
                    }

                    // Start new stream at current string and position
                    activeStreamID = if (cachedHighestPressedFinger == -1) 0 else play(string, calculateNoteMapIndex(cachedHandPosition, cachedHighestPressedFinger))
                    activeStreamVolume = 0f
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
                    activeStreamID = 0
                    activeStreamVolume = 0f
                }
            }

            // Increase active stream volume
            if (activeStreamID > 0 && activeStreamVolume < MAX_VOLUME) {

                activeStreamVolume += if (fadingStreamID > 0)
                    MAX_VOLUME * SOUND_UPDATE_DELAY_MS / blendTime
                else MAX_VOLUME * SOUND_UPDATE_DELAY_MS / fadeInTime

                setVolume(activeStreamID, min(MAX_VOLUME, activeStreamVolume))
            }

            // Decrease fading stream volume
            if (fadingStreamID > 0 && fadingStreamVolume > 0 && !isTimerRunning) {

                fadingStreamVolume -= if (cachedHighestPressedFinger > -1 || activeStreamID > 0)
                    MAX_VOLUME * SOUND_UPDATE_DELAY_MS / blendTime
                else
                    MAX_VOLUME * SOUND_UPDATE_DELAY_MS / fadeOutTime

                setVolume(fadingStreamID, max(0f, fadingStreamVolume))
            }

            else if (fadingStreamID > 0 && fadingStreamVolume <= 0) {
                stop(fadingStreamID)
                fadingStreamID = 0
                fadingStreamVolume = 0f
            }

            delay(SOUND_UPDATE_DELAY_MS.milliseconds)
        }
    }


    suspend fun manageGString () {

        withContext(defaultDispatcher) {
            launch {
                manageString(ViolinString.G)
            }
        }
    }

    suspend fun manageDString () {

        withContext(defaultDispatcher) {
            launch {
                manageString(ViolinString.D)
            }
        }
    }

    suspend fun manageAString () {

        withContext(defaultDispatcher) {
            launch {
                manageString(ViolinString.A)
            }
        }
    }

    suspend fun manageEString () {

        withContext(defaultDispatcher) {
            launch {
                manageString(ViolinString.E)
            }
        }
    }

    suspend fun manageTimer () {

        withContext(defaultDispatcher) {

            var time: Int = fadeOutDelay

            while (true) {

                if (!isTimerRunning) continue

                if (time <= 0) {
                    isTimerRunning = false
                    time = fadeOutDelay
                }

                time -= SOUND_UPDATE_DELAY_MS

                delay(SOUND_UPDATE_DELAY_MS.milliseconds)
            }
        }
    }

    fun setFadeInTime (newFadeInTime: Int) {
        fadeInTime = newFadeInTime
    }

    fun setBlendTime (newBlendTime: Int) {
        blendTime = newBlendTime
    }

    fun setFadeOutTime (newFadeOutTime: Int) {
        fadeOutTime = newFadeOutTime
    }

    fun setFadeOutDelay (newFadeOutDelay: Int) {
        fadeOutDelay = newFadeOutDelay
    }

    private fun play (string: ViolinString, position: Int, volume: Float = 0f) : Int {

        /*
        Trying out dynamically produced sound centred around A4 (440 Hz).

        Each semitone is a 100 cent interval, and since an octave has 12 semitones,
        there are 1200 cents per octave, so 100 cents = 1 / 12 octaves.

        So, a note n semitones from A4 has the rate 2^(n / 12).
        */

        val noteMapKey: Int = string.ordinal * 10 + position / 12
        val streamID = soundPool.play(
            noteSoundFileHashMap[noteMapKey]!!,
            volume,
            volume,
            0,
            -1,
            2.0.pow((position % 12) / 12.0).toFloat()
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
        soundPool.release()
    }
}