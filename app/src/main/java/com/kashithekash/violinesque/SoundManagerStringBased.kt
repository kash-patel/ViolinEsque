import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import com.kashithekash.violinesque.R
import com.kashithekash.violinesque.ViolinString
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

private const val START_BOWING_FADE_IN_TIME_MS = 30
private const val NOTE_TRANSITION_TIME_MS = 50
private const val PLACE_MOVING_BOW_FADE_IN_TIME_MS = 10
private const val LIFT_BOW_FADE_OUT_TIME_MS = 100

private val isButtonTouched: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false, false, false, false, false, false)
//private val isStringActive: BooleanArray = booleanArrayOf(false, false, true, false)
private var activeString: ViolinString? = null
private var activePosition: Int = -1

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

        isButtonTouched[position] = true

        if (position <= activePosition) return

        activePosition = position
    }

    fun handleButtonRelease (position: Int) {

        isButtonTouched[position] = false

        if (position != activePosition) return

        activePosition = getHighestPosition()
    }

    fun handleStringChange (newString: ViolinString) {

//        isStringActive[activeString.ordinal] = false
//        isStringActive[newString.ordinal] = true
        activeString = newString
    }

    private suspend fun manageString (string: ViolinString) {

        var cachedPosition: Int = -1
        var activeStreamID: Int = 0
        var fadingStreamID: Int = 0
        var activeStreamVolume: Float = 0f
        var fadingStreamVolume: Float = 0f


        while (true) {

            if (activeString == null) continue

            // Starting streams
            if (activeString == string) {

                // Position change
                if  (cachedPosition != activePosition) {

                    cachedPosition = activePosition
                    Log.w("SoundManagerStringBased", "Active: $string $cachedPosition")

                    // Shift streams if something is already playing
                    if (activeStreamID > 0) {
                        if (fadingStreamID > 0) stop(fadingStreamID)
                        fadingStreamID = activeStreamID
                        fadingStreamVolume = activeStreamVolume
                    }

                    // Start new stream at current string and position
                    activeStreamID = if (cachedPosition == -1) 0 else play(string, cachedPosition)
                    activeStreamVolume = 0f
                }
            }

            // Fading out and stopping streams
            else {

                // Reset position
                cachedPosition = -1

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

                if (fadingStreamID > 0 && fadingStreamVolume > 0.8f)
                    activeStreamVolume += MAX_VOLUME * SOUND_UPDATE_DELAY_MS / NOTE_TRANSITION_TIME_MS
                else activeStreamVolume += MAX_VOLUME * SOUND_UPDATE_DELAY_MS / START_BOWING_FADE_IN_TIME_MS

                setVolume(activeStreamID, min(MAX_VOLUME, activeStreamVolume))
            }

            // Decrease fading stream volume
            if (fadingStreamID > 0 && fadingStreamVolume > 0) {

                if (cachedPosition > -1)
                    fadingStreamVolume -= MAX_VOLUME * SOUND_UPDATE_DELAY_MS / NOTE_TRANSITION_TIME_MS
                else
                    fadingStreamVolume -= MAX_VOLUME * SOUND_UPDATE_DELAY_MS / LIFT_BOW_FADE_OUT_TIME_MS

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

    private fun play (violinString: ViolinString, buttonNumber: Int, volume: Float = 0f) : Int {

        val streamID = soundPool.play(
            noteSoundFileHashMap[violinString.ordinal * 100 + buttonNumber]!!,
            volume,
            volume,
            0,
            -1,
            1f
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