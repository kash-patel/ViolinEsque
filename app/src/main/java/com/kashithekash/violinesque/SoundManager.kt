package com.kashithekash.violinesque

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

private const val SOUND_UPDATE_DELAY_MS = 10
private const val MAX_VOLUME : Float = 1f
private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

private const val START_BOWING_FADE_IN_TIME_MS = 50f
private const val NOTE_TRANSITION_TIME_MS = 30f
private const val PLACE_MOVING_BOW_FADE_IN_TIME_MS = 30f
private const val PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME = 30f
private const val LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME = 30f
private const val LIFT_BOW_FADE_OUT_TIME = 100f

private val isButtonTouched: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false, false, false, false, false, false)
private var activePostion: Int = -1
private var fadingPosition: Int = -1
private var activeString: ViolinString = ViolinString.A
private var fadingString: ViolinString = ViolinString.A
private var activeStreamID: Int = 0
private var fadingPositionStreamID: Int = 0
private var fadingStringStreamID: Int = 0
private var terminalStreams: MutableList<Int> = mutableListOf<Int>()
private var activeStreamVolume: Float = 0f
private var fadingPositionStreamVolume: Float = 0f
private var fadingStringStreamVolume: Float = 0f

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

class SoundManager (context: Context) {

    init {
        loadSoundFiles(context)
    }

    fun handleButtonTouch (position: Int) {

        isButtonTouched[position] = true

        if (position <= activePostion) return

        fadingPosition = activePostion
        activePostion = position

        fadingPositionStreamVolume = activeStreamVolume
        activeStreamVolume = 0f

        fadingString = activeString

        if (fadingPositionStreamID > 0) terminalStreams.add(fadingPositionStreamID)
        fadingPositionStreamID = activeStreamID
        activeStreamID = play(activeString, activePostion, activeStreamVolume)

//        Log.w("SoundManager", "Fading: $fadingString $fadingPosition $fadingPositionStreamID Active: $activeString $activePostion $activeStreamID.")
    }

    fun handleButtonRelease (position: Int) {

        isButtonTouched[position] = false

        if (position != activePostion) return

        fadingPosition = activePostion
        activePostion = getHighestPosition()

        fadingPositionStreamVolume = activeStreamVolume
        activeStreamVolume = 0f

        fadingString = activeString

        if (fadingPositionStreamID > 0) terminalStreams.add(fadingPositionStreamID)
        fadingPositionStreamID = activeStreamID
        activeStreamID = if (activePostion == -1) 0 else play(activeString, activePostion, activeStreamVolume)

//        Log.w("SoundManager", "Fading: $fadingString $fadingPosition $fadingPositionStreamID Active: $activeString $activePostion $activeStreamID.")
    }

    fun handleStringChange (newString: ViolinString) {

        fadingString = activeString
        activeString = newString

        fadingPosition = activePostion
        activePostion = getHighestPosition()

        fadingStringStreamVolume = activeStreamVolume
        activeStreamVolume = 0f

        if (fadingStringStreamID > 0) terminalStreams.add(fadingStringStreamID)
        fadingStringStreamID = activeStreamID
        activeStreamID =
            if (activePostion == -1) 0
            else play(newString, activePostion, activeStreamVolume)

//        Log.w("SoundManager", "Fading: $fadingString $fadingPosition $fadingPositionStreamID Active: $activeString $activePostion $activeStreamID.")
    }

    suspend fun manageActiveStream () {

        withContext(defaultDispatcher) {

            launch {

                while (true) {

                    if (activeStreamID == 0 || activeStreamVolume >= MAX_VOLUME) {
                        delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                        continue
                    }

                    // Playing on a string with no active positions
                    if (fadingPosition == -1 || fadingPosition != activePostion && fadingString != activeString)
                        activeStreamVolume += MAX_VOLUME * SOUND_UPDATE_DELAY_MS / START_BOWING_FADE_IN_TIME_MS
                    // Playing on a string with active lower positions
                    else if (fadingPosition > -1 && fadingPosition != activePostion && fadingString == activeString)
                        activeStreamVolume += MAX_VOLUME * SOUND_UPDATE_DELAY_MS / NOTE_TRANSITION_TIME_MS
                    // Shifting to a new string while playing the same position
                    else if (fadingPosition > -1 && fadingPosition == activePostion && activeString != fadingString)
                        activeStreamVolume += MAX_VOLUME * SOUND_UPDATE_DELAY_MS / PLACE_MOVING_BOW_FADE_IN_TIME_MS

                    setVolume(activeStreamID, min(1f, activeStreamVolume))

                    delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                }
            }
        }
    }

    suspend fun manageFadingPositionStream () {

        withContext(defaultDispatcher) {

            launch {

                while (true) {

                    if (fadingPositionStreamID == 0) {
                        delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                        continue
                    }

                    if (fadingPositionStreamVolume <= 0) {

                        fadingPositionStreamVolume = 0f
                        terminalStreams.add(fadingPositionStreamID)
                        fadingPositionStreamID = 0

                        delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                        continue
                    }

                    // Fading out a position being replaced with a new one
                    if (activePostion > -1 && fadingPosition != activePostion && activeString == fadingString)
                        fadingPositionStreamVolume -= MAX_VOLUME * SOUND_UPDATE_DELAY_MS / NOTE_TRANSITION_TIME_MS
                    else if (activePostion == -1)
                        fadingPositionStreamVolume -= MAX_VOLUME * SOUND_UPDATE_DELAY_MS / LIFT_BOW_FADE_OUT_TIME

                    setVolume(fadingPositionStreamID, max(0f, fadingPositionStreamVolume))

                    delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                }
            }
        }
    }

    suspend fun manageFadingStringStream () {

        withContext(defaultDispatcher) {

            launch {

                while (true) {

                    if (fadingStringStreamID == 0) {
                        delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                        continue
                    }

                    if (fadingStringStreamVolume <= 0) {

                        fadingStringStreamVolume = 0f
                        terminalStreams.add(fadingStringStreamID)
                        fadingStringStreamID = 0

                        delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                        continue
                    }
                    
                    fadingStringStreamVolume -= MAX_VOLUME * SOUND_UPDATE_DELAY_MS / LIFT_BOW_FADE_OUT_TIME

                    setVolume(fadingStringStreamID, max(0f, fadingStringStreamVolume))

                    delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                }
            }
        }
    }

    suspend fun manageTerminalStreams () {

        withContext(defaultDispatcher) {

            launch {

                while (true) {

                    if (terminalStreams.isNotEmpty())
                        stop(terminalStreams.removeFirst())

                    delay(SOUND_UPDATE_DELAY_MS.milliseconds)
                }
            }
        }
    }

    private fun play (violinString: ViolinString, buttonNumber: Int, volume: Float = MAX_VOLUME) : Int {

        val streamID = soundPool.play(
            noteSoundFileHashMap[violinString.ordinal * 100 + buttonNumber]!!,
            volume,
            volume,
            0,
            -1,
            1f
        )

//        Log.w("SoundManager", "Started $streamID")

        return streamID
    }

    private fun stop (streamID: Int) {
        soundPool.stop(streamID)
//        Log.w("SoundManager", "Stopped $streamID.")
    }

    private fun setVolume (streamID: Int, newVolume: Float) {
        soundPool.setVolume(streamID, newVolume, newVolume)
//        Log.w("SoundManager", "Set volume of $streamID to $newVolume.")
    }

    private fun getHighestPosition () : Int {

        var maxIndex: Int = -1

        for (i in isButtonTouched.indices)
            if (isButtonTouched[i]) maxIndex = i

        return maxIndex
    }

    private fun loadSoundFiles (context: Context) {

        noteSoundFileHashMap[ViolinString.G.ordinal * 100] = soundPool.load(context, R.raw.g3, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 1] = soundPool.load(context, R.raw.gsharp3, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 2] = soundPool.load(context, R.raw.a3, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 3] = soundPool.load(context, R.raw.asharp3, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 4] = soundPool.load(context, R.raw.b3, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 5] = soundPool.load(context, R.raw.c4, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 6] = soundPool.load(context, R.raw.csharp4, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 7] = soundPool.load(context, R.raw.d4, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 8] = soundPool.load(context, R.raw.dsharp4, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 9] = soundPool.load(context, R.raw.e4, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 10] = soundPool.load(context, R.raw.f4, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 11] = soundPool.load(context, R.raw.fsharp4, 0)
        noteSoundFileHashMap[ViolinString.G.ordinal * 100 + 12] = soundPool.load(context, R.raw.g4, 0)

        noteSoundFileHashMap[ViolinString.D.ordinal * 100] = soundPool.load(context, R.raw.d4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 1] = soundPool.load(context, R.raw.dsharp4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 2] = soundPool.load(context, R.raw.e4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 3] = soundPool.load(context, R.raw.f4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 4] = soundPool.load(context, R.raw.fsharp4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 5] = soundPool.load(context, R.raw.g4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 6] = soundPool.load(context, R.raw.gsharp4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 7] = soundPool.load(context, R.raw.a4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 8] = soundPool.load(context, R.raw.asharp4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 9] = soundPool.load(context, R.raw.b4, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 10] = soundPool.load(context, R.raw.c5, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 11] = soundPool.load(context, R.raw.csharp5, 0)
        noteSoundFileHashMap[ViolinString.D.ordinal * 100 + 12] = soundPool.load(context, R.raw.d5, 0)

        noteSoundFileHashMap[ViolinString.A.ordinal * 100] = soundPool.load(context, R.raw.a4, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 1] = soundPool.load(context, R.raw.asharp4, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 2] = soundPool.load(context, R.raw.b4, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 3] = soundPool.load(context, R.raw.c5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 4] = soundPool.load(context, R.raw.csharp5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 5] = soundPool.load(context, R.raw.d5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 6] = soundPool.load(context, R.raw.dsharp5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 7] = soundPool.load(context, R.raw.e5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 8] = soundPool.load(context, R.raw.f5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 9] = soundPool.load(context, R.raw.fsharp5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 10] = soundPool.load(context, R.raw.g5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 11] = soundPool.load(context, R.raw.gsharp5, 0)
        noteSoundFileHashMap[ViolinString.A.ordinal * 100 + 12] = soundPool.load(context, R.raw.a5, 0)

        noteSoundFileHashMap[ViolinString.E.ordinal * 100] = soundPool.load(context, R.raw.e5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 1] = soundPool.load(context, R.raw.f5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 2] = soundPool.load(context, R.raw.fsharp5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 3] = soundPool.load(context, R.raw.g5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 4] = soundPool.load(context, R.raw.gsharp5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 5] = soundPool.load(context, R.raw.a5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 6] = soundPool.load(context, R.raw.asharp5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 7] = soundPool.load(context, R.raw.b5, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 8] = soundPool.load(context, R.raw.c6, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 9] = soundPool.load(context, R.raw.csharp6, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 10] = soundPool.load(context, R.raw.d6, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 11] = soundPool.load(context, R.raw.dsharp6, 0)
        noteSoundFileHashMap[ViolinString.E.ordinal * 100 + 12] = soundPool.load(context, R.raw.e6, 0)
    }

    fun release () {
        soundPool.release()
    }
}