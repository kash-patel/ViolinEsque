package com.kashithekash.violinesque

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import kotlin.concurrent.thread


private const val MAX_VOLUME : Float = 1f

private const val START_BOWING_FADE_IN_TIME_MS = 50f
private const val PLACE_MOVING_BOW_FADE_IN_TIME_MS = 30f
private const val PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME = 60f
private const val LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME = 60f
private const val LIFT_BOW_FADE_OUT_TIME = 100f

private val isButtonTouched : BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false, false, false, false, false, false)
private val activeStreamIDs : IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
private val activeStreamVolumes : FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
private val fadingStreamIDs : IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
private val fadingStreamVolumes : FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

private val soundPool : SoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

    val audioAttributes : AudioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_GAME)
        .build()

    SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(audioAttributes)
        .build()

} else {
    SoundPool(2, AudioManager.USE_DEFAULT_STREAM_TYPE, 0)
}

private val noteSoundFileHashMap : HashMap<Int, Int> = HashMap(1)

class SoundManager (context: Context) {

    init {
        loadSoundFiles(context)
    }

    fun handleButtonTouched (currentString: ViolinString, buttonNumber: Int) {

        val currentHighestPosition: Int = maxNonZeroIndex(isButtonTouched)
        isButtonTouched[buttonNumber] = true

        if (currentHighestPosition < buttonNumber) {

            if (currentHighestPosition > -1) {
                placeFingerWhileBowing(currentString, buttonNumber, currentHighestPosition)
            }
            else {
                startBowing(currentString, buttonNumber)
            }
        }
    }

    fun handleStringChange (newString: ViolinString) {

        val currentHighestPosition: Int = maxNonZeroIndex(isButtonTouched)

        if (currentHighestPosition > -1)
            changeStringWhileBowing(newString, currentHighestPosition)
    }

    fun handleButtonReleased (currentString: ViolinString, buttonNumber: Int) {

        isButtonTouched[buttonNumber] = false

        val currentHighestPosition: Int = maxNonZeroIndex(isButtonTouched)

        if (buttonNumber > currentHighestPosition) {
            if (currentHighestPosition > -1) {
                liftFingerWhileBowing(currentString, buttonNumber, currentHighestPosition)
            }
            else {
                liftBow(buttonNumber)
            }
        }
    }

    private fun startBowing (currentString: ViolinString, buttonNumber: Int) {

        fadingStreamIDs[buttonNumber] = activeStreamIDs[buttonNumber]
        fadingStreamVolumes[buttonNumber] = activeStreamVolumes[buttonNumber]

        activeStreamIDs[buttonNumber] = play(currentString, buttonNumber, fadingStreamVolumes[buttonNumber])
        activeStreamVolumes[buttonNumber] = fadingStreamVolumes[buttonNumber]

        thread {

            while (activeStreamVolumes[buttonNumber] < MAX_VOLUME) {

                activeStreamVolumes[buttonNumber] += (MAX_VOLUME / (START_BOWING_FADE_IN_TIME_MS / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(activeStreamIDs[buttonNumber], activeStreamVolumes[buttonNumber])
            }
        }
    }

    private fun changeStringWhileBowing (newString: ViolinString, buttonNumber: Int) {

        fadingStreamIDs[buttonNumber] = activeStreamIDs[buttonNumber]
        activeStreamIDs[buttonNumber] = play(newString, buttonNumber, fadingStreamVolumes[buttonNumber])

        val tempVolume = activeStreamVolumes[buttonNumber]
        activeStreamVolumes[buttonNumber] = fadingStreamVolumes[buttonNumber]
        fadingStreamVolumes[buttonNumber] = tempVolume

        // To avoid race conditions
        val localFadingStreamID = fadingStreamIDs[buttonNumber]
        val localActiveStreamID = activeStreamIDs[buttonNumber]

        // Fade old string out
        thread {

            while (fadingStreamVolumes[buttonNumber] > 0) {

                fadingStreamVolumes[buttonNumber] -= (MAX_VOLUME / (LIFT_BOW_FADE_OUT_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(localFadingStreamID, fadingStreamVolumes[buttonNumber])
            }

            stop(localFadingStreamID)
        }

        // Fade new string in
        thread {

            while (isButtonTouched[buttonNumber] && activeStreamVolumes[buttonNumber] < MAX_VOLUME) {

                activeStreamVolumes[buttonNumber] += (MAX_VOLUME / (PLACE_MOVING_BOW_FADE_IN_TIME_MS / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(localActiveStreamID, activeStreamVolumes[buttonNumber])
            }
        }
    }

    private fun placeFingerWhileBowing (currentString: ViolinString, buttonNumber: Int, previousHighestPosition: Int) {

        fadingStreamIDs[previousHighestPosition] = activeStreamIDs[previousHighestPosition]
        activeStreamIDs[buttonNumber] = play(currentString, buttonNumber, fadingStreamVolumes[buttonNumber])

        activeStreamVolumes[buttonNumber] = fadingStreamVolumes[buttonNumber]
        fadingStreamVolumes[previousHighestPosition] = activeStreamVolumes[previousHighestPosition]

        thread {

            while (isButtonTouched[buttonNumber] && activeStreamVolumes[buttonNumber] < MAX_VOLUME) {

                activeStreamVolumes[buttonNumber] += (MAX_VOLUME / (PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME / 10f))
                fadingStreamVolumes[previousHighestPosition] -= (MAX_VOLUME / (PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(fadingStreamIDs[previousHighestPosition], fadingStreamVolumes[previousHighestPosition])
                setVolume(activeStreamIDs[buttonNumber], activeStreamVolumes[buttonNumber])
            }

            stop(fadingStreamIDs[previousHighestPosition])
        }
    }

    private fun liftFingerWhileBowing (currentString: ViolinString, buttonNumber: Int, newHighestPosition: Int) {

        fadingStreamIDs[buttonNumber] = activeStreamIDs[buttonNumber]
        activeStreamIDs[newHighestPosition] = play(currentString, newHighestPosition, fadingStreamVolumes[newHighestPosition])

        activeStreamVolumes[newHighestPosition] = fadingStreamVolumes[newHighestPosition]
        fadingStreamVolumes[buttonNumber] = activeStreamVolumes[buttonNumber]

        thread {

            while (isButtonTouched[newHighestPosition] && activeStreamVolumes[newHighestPosition] < MAX_VOLUME) {

                fadingStreamVolumes[buttonNumber] -= (MAX_VOLUME / (LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME / 10f))
                activeStreamVolumes[newHighestPosition] += (MAX_VOLUME / (LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(fadingStreamIDs[buttonNumber], fadingStreamVolumes[buttonNumber])
                setVolume(activeStreamIDs[newHighestPosition], activeStreamVolumes[newHighestPosition])
            }

            stop(fadingStreamIDs[buttonNumber])
        }
    }

    private fun liftBow (buttonNumber: Int) {

        fadingStreamIDs[buttonNumber] = activeStreamIDs[buttonNumber]
        fadingStreamVolumes[buttonNumber] = activeStreamVolumes[buttonNumber]

        thread {

            while (!isButtonTouched[buttonNumber] && fadingStreamVolumes[buttonNumber] > 0) {

                fadingStreamVolumes[buttonNumber] -= (MAX_VOLUME / (LIFT_BOW_FADE_OUT_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(fadingStreamIDs[buttonNumber], fadingStreamVolumes[buttonNumber])
            }

            stop(fadingStreamIDs[buttonNumber])
        }
    }

    private fun play (violinString: ViolinString, buttonNumber: Int, volume: Float = MAX_VOLUME) : Int {

        return soundPool.play(
            noteSoundFileHashMap[violinString.ordinal * 100 + buttonNumber]!!,
            volume,
            volume,
            1,
            -1,
            1f
        )
    }

    private fun stop (streamID: Int) {
        soundPool.stop(streamID)
    }

    private fun setVolume (streamID: Int, newVolume: Float) {
        soundPool.setVolume(streamID, newVolume, newVolume)
    }

    private fun maxNonZeroIndex (arr: BooleanArray) : Int {

        var maxIndex : Int = -1

        for (i in arr.indices)
            if (arr[i]) maxIndex = i

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

}