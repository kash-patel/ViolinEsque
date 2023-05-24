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
private const val PLACE_MOVING_BOW_FADE_IN_TIME_MS = 30f
private const val PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME = 60f
private const val LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME = 60f
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
        activeStreamIDs[buttonNumber] = streamID
        var volume: Float = streamVolumes[buttonNumber]

        thread {

            while (buttonStates[buttonNumber] && volume < MAX_VOLUME) {

                volume += (MAX_VOLUME / (START_BOWING_FADE_IN_TIME_MS / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(streamID, volume)
            }

            streamVolumes[buttonNumber] = volume
        }
    }

    private fun changeStringWhileBowing (newString: ViolinString, buttonNumber: Int) {

        val outStreamID = activeStreamIDs[buttonNumber]
        var outStreamVolume = streamVolumes[buttonNumber]
        val inStreamID = play(newString, buttonNumber, 0f)
        var inStreamVolume = 0f

        // Fade old string out
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

            stop(outStreamID)
        }

        // Fade new string in
        thread {

            while (buttonStates[buttonNumber] && inStreamVolume < MAX_VOLUME) {

                inStreamVolume += (MAX_VOLUME / (PLACE_MOVING_BOW_FADE_IN_TIME_MS / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(inStreamID, inStreamVolume)
            }

            activeStreamIDs[buttonNumber] = inStreamID
            streamVolumes[buttonNumber] = inStreamVolume
        }
    }

    private fun placeFingerWhileBowing (currentString: ViolinString, buttonNumber: Int, previousHighestPosition: Int) {

        val outStreamID = activeStreamIDs[previousHighestPosition]
        val inStreamID = play(currentString, buttonNumber, streamVolumes[buttonNumber])
        var outStreamVolume = streamVolumes[previousHighestPosition]
        var inStreamVolume = streamVolumes[buttonNumber]
        activeStreamIDs[buttonNumber] = inStreamID

        thread {

            while (buttonStates[buttonNumber] && inStreamVolume < MAX_VOLUME) {

                inStreamVolume += (MAX_VOLUME / (PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME / 10f))
                outStreamVolume -= (MAX_VOLUME / (PLACE_FINGER_WITH_BOW_MOVING_FADE_IN_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(outStreamID, outStreamVolume)
                setVolume(inStreamID, inStreamVolume)
            }

            streamVolumes[previousHighestPosition] = 0f
            streamVolumes[buttonNumber] = inStreamVolume
            stop(outStreamID)
            activeStreamIDs[previousHighestPosition] = 0
        }
    }

    private fun liftFingerWhileBowing (currentString: ViolinString, buttonNumber: Int, newHighestPosition: Int) {

        val outStreamID = activeStreamIDs[buttonNumber]
        val inStreamID = play(currentString, newHighestPosition, streamVolumes[newHighestPosition])
        var outStreamVolume = streamVolumes[buttonNumber]
        var inStreamVolume = streamVolumes[newHighestPosition]
        activeStreamIDs[newHighestPosition] = inStreamID

        thread {

            while (buttonStates[newHighestPosition] && inStreamVolume < MAX_VOLUME) {

                outStreamVolume -= (MAX_VOLUME / (LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME / 10f))
                inStreamVolume += (MAX_VOLUME / (LIFT_FINGER_WITH_BOW_MOVING_FADE_OUT_TIME / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                setVolume(outStreamID, outStreamVolume)
                setVolume(inStreamID, inStreamVolume)
            }

            streamVolumes[buttonNumber] = 0f
            streamVolumes[newHighestPosition] = inStreamVolume
            stop(outStreamID)
            activeStreamIDs[buttonNumber] = 0
        }
    }

    private fun liftBow (buttonNumber: Int) {

        val streamID: Int = activeStreamIDs[buttonNumber]
        var volume = streamVolumes[buttonNumber]

        thread {

            while (!buttonStates[buttonNumber] && volume > 0) {

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
            activeStreamIDs[buttonNumber] = 0
        }
    }

    private fun play (violinString: ViolinString, buttonNumber: Int, volume: Float = MAX_VOLUME) : Int {

        return soundPool.play(
            noteSoundFileHashMap[violinString.ordinal * 100 + buttonNumber]!!,
            volume,
            volume,
            1,
            1,
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