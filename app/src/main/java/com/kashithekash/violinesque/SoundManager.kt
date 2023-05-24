package com.kashithekash.violinesque

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import kotlin.concurrent.thread


private const val MAX_VOLUME : Float = 1f

private const val START_BOWING_FADE_IN_TIME_MS = 50f
private const val PLACE_MOVING_BOW_FADE_IN_TIME_MS = 10f
private const val PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME = 30f
private const val LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME = 30f
private const val LIFT_BOW_FADE_OUT_TIME = 100f

private val buttonStates : BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false, false, false, false, false, false)
private val activeStreamIDs : IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
private val streamVolumes : FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

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

        val currentHighestPosition: Int = maxNonZeroIndex(buttonStates)
        buttonStates[buttonNumber] = true

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

        val currentHighestPosition: Int = maxNonZeroIndex(buttonStates)

        if (currentHighestPosition > -1)
            changeStringWhileBowing(newString, currentHighestPosition)
    }

    fun handleButtonReleased (currentString: ViolinString, buttonNumber: Int) {

        buttonStates[buttonNumber] = false

        val currentHighestPosition: Int = maxNonZeroIndex(buttonStates)

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

        val streamID: Int = play(currentString, buttonNumber, 0f)
        var volume = 0f
        activeStreamIDs[buttonNumber] = streamID

        thread {

            while (volume < MAX_VOLUME) {

                volume += (MAX_VOLUME / (START_BOWING_FADE_IN_TIME_MS / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(streamID, volume)
            }
        }
    }

    private fun changeStringWhileBowing (newString: ViolinString, buttonNumber: Int) {

        val outStreamID = activeStreamIDs[buttonNumber]
        var outStreamVolume = streamVolumes[buttonNumber]
        val inStreamID = play(newString, buttonNumber, streamVolumes[buttonNumber])
        activeStreamIDs[buttonNumber] = inStreamID
        streamVolumes[buttonNumber] = 0f

        // Fade out old string
        thread {

            while (outStreamVolume > 0) {

                outStreamVolume -= (MAX_VOLUME / (LIFT_BOW_FADE_OUT_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(outStreamID, outStreamVolume)
            }

            outStreamVolume = 0f
            stop(outStreamID)
        }

        // Fade in new string
        thread {

            while (streamVolumes[buttonNumber] < MAX_VOLUME) {

                streamVolumes[buttonNumber] += (MAX_VOLUME / (PLACE_MOVING_BOW_FADE_IN_TIME_MS / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(inStreamID, streamVolumes[buttonNumber])
            }
        }
    }

    private fun placeFingerWhileBowing (currentString: ViolinString, buttonNumber: Int, previousHighestPosition: Int) {

        val outStreamID = activeStreamIDs[previousHighestPosition]
        val inStreamID = play(currentString, buttonNumber, streamVolumes[buttonNumber])
        var volume = 0f
        activeStreamIDs[buttonNumber] = inStreamID

        activeStreamIDs[previousHighestPosition] = 0
        streamVolumes[previousHighestPosition] = 0f
        stop(outStreamID)

        thread {

            while (volume < MAX_VOLUME) {

                volume += (MAX_VOLUME / (PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(inStreamID, volume)
            }

            streamVolumes[buttonNumber] = MAX_VOLUME
        }
    }

    private fun liftFingerWhileBowing (currentString: ViolinString, buttonNumber: Int, newHighestPosition: Int) {

        val outStreamID = activeStreamIDs[buttonNumber]
        var volume = streamVolumes[buttonNumber]
        var inStreamID = 0

        thread {

            while (volume > 0) {

                volume -= (MAX_VOLUME / (LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(outStreamID, volume)
            }

            streamVolumes[buttonNumber] = 0f
            activeStreamIDs[buttonNumber] = 0
            stop(outStreamID)
        }

        thread {

            Thread.sleep(LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME.toLong())

            // It might have changed!
            if (maxNonZeroIndex(buttonStates) == newHighestPosition) {

                inStreamID = play(currentString, newHighestPosition, MAX_VOLUME)
                activeStreamIDs[newHighestPosition] = inStreamID
                streamVolumes[newHighestPosition] = MAX_VOLUME
            }
        }
    }

    private fun liftBow (buttonNumber: Int) {

        val streamID: Int = activeStreamIDs[buttonNumber]
        var volume = streamVolumes[buttonNumber]
        activeStreamIDs[buttonNumber] = 0

        thread {

            while (volume > 0) {

                volume -= (MAX_VOLUME / (LIFT_BOW_FADE_OUT_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(streamID, volume)
            }

            streamVolumes[buttonNumber] = 0f
            stop(streamID)
        }
    }

    private fun play (violinString: ViolinString, buttonNumber: Int, volume: Float) : Int {

        return soundPool.play(
            noteSoundFileHashMap[violinString.ordinal * 100 + buttonNumber]!!,
            volume,
            volume,
            1,
            1,
            1F
        )
    }

    private fun stop (streamID: Int) {
        soundPool.stop(streamID)
    }

    private fun setVolume (streamID: Int, newVolume: Float) {
        soundPool.setVolume(streamID, newVolume, newVolume)
    }

    private fun setRate (streamID: Int, newRate: Float) {
        soundPool.setRate(streamID, newRate)
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