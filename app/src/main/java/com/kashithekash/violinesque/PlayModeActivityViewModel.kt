import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.Config
import com.kashithekash.violinesque.Interactability
import com.kashithekash.violinesque.PrefRepo
import com.kashithekash.violinesque.RotationReader
import com.kashithekash.violinesque.SoundManager
import com.kashithekash.violinesque.StringManager
import com.kashithekash.violinesque.ViolinString
import kotlin.concurrent.thread

class PlayModeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val MAX_VOLUME : Float = 1f
    private val FADE_IN_TIME_MS = 10f
    private val BLEND_TIME_MS = 50f
    private val FADE_OUT_TIME_MS = 100f

    private val rotationReader : RotationReader = RotationReader(context = application)
    private val stringManager : StringManager = StringManager()
    private val soundManager : SoundManager = SoundManager(context = application)

    private var currentString: MutableLiveData<ViolinString> = MutableLiveData<ViolinString>(ViolinString.A)

    private val buttonStates : BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false, false, false, false, false, false)
    private val activeStreamIDs : IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private val streamVolumes : FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val blendInVolumes : FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    private val config: Config = Config
    private val prefRepo: PrefRepo = PrefRepo(application.applicationContext)

    fun buttonTouched (currentString: ViolinString, buttonNumber: Int) {

        val currentHighestPosition : Int = maxNonZeroIndex(buttonStates)

        if (currentHighestPosition < buttonNumber) {

            if (currentHighestPosition > -1) stopNote(currentHighestPosition)

            if (streamVolumes[buttonNumber] <= 0.1f)
                playNote(currentString, buttonNumber)
            else
                blendInNote(currentString, buttonNumber)
        }

        buttonStates[buttonNumber] = true
    }

    fun buttonReleased (currentString: ViolinString, buttonNumber: Int) {

        buttonStates[buttonNumber] = false

        val currentHighestPosition : Int = maxNonZeroIndex(buttonStates)

        if (buttonNumber > currentHighestPosition) {
            stopNote(buttonNumber)
            if (currentHighestPosition > -1) blendInNote(currentString, currentHighestPosition)
        }
    }

    fun stringChanged (newString: ViolinString) {

        val currentHighestPosition: Int = maxNonZeroIndex(buttonStates)

        if (currentHighestPosition > -1) blendInNote(newString, currentHighestPosition)
    }

    fun updateCurrentString (calibratedRoll : Float) {
        currentString.value = stringManager.calculateCurrentString(calibratedRoll)
    }

    fun getCurrentStringLiveData () : LiveData<ViolinString> {
        return currentString
    }

    fun getConfigStateLiveData () : LiveData<Long> {
        return config.configState
    }
    fun getButtonInteractabilityArray () : Array<Interactability> {
        return config.buttonInteractabilities
    }

    fun getRotationVector () = rotationReader

    fun loadConfig () {

        val savedButtonInteractabilities: Array<Interactability> = arrayOf(
            prefRepo.getButtonInteractability(0),
            prefRepo.getButtonInteractability(1),
            prefRepo.getButtonInteractability(2),
            prefRepo.getButtonInteractability(3),
            prefRepo.getButtonInteractability(4),
            prefRepo.getButtonInteractability(5),
            prefRepo.getButtonInteractability(6),
            prefRepo.getButtonInteractability(7),
            prefRepo.getButtonInteractability(8),
            prefRepo.getButtonInteractability(9),
            prefRepo.getButtonInteractability(10),
            prefRepo.getButtonInteractability(11),
            prefRepo.getButtonInteractability(12)
        )

        config.init(savedButtonInteractabilities)
    }

    private fun playNote (currentString: ViolinString, buttonNumber: Int) {

        val streamID: Int = soundManager.play(currentString, buttonNumber, streamVolumes[buttonNumber])
        activeStreamIDs[buttonNumber] = streamID
        fadeIn(streamID, buttonNumber)
    }

    private fun blendInNote (currentString: ViolinString, buttonNumber: Int) {

        val outSteamID = activeStreamIDs[buttonNumber]
        val inStreamID = soundManager.play(currentString, buttonNumber, streamVolumes[buttonNumber])

        blend(inStreamID, outSteamID, buttonNumber)

        activeStreamIDs[buttonNumber] = inStreamID
    }

    private fun stopNote (buttonNumber: Int) {

        val streamID: Int = activeStreamIDs[buttonNumber]
        activeStreamIDs[buttonNumber] = 0
        fadeOut(streamID, buttonNumber)
    }

    private fun fadeIn (streamID: Int, buttonNumber: Int) {

        thread {

            while (maxNonZeroIndex(buttonStates) > -1 && streamVolumes[buttonNumber] < MAX_VOLUME) {

                streamVolumes[buttonNumber] += (MAX_VOLUME / (FADE_IN_TIME_MS / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                soundManager.setVolume(streamID, streamVolumes[buttonNumber])
            }
        }
    }

    private fun fadeOut (streamID: Int, buttonNumber: Int) {

        thread {

            while (streamVolumes[buttonNumber] > 0) {

                streamVolumes[buttonNumber] -= (MAX_VOLUME / (FADE_OUT_TIME_MS / 10f))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                soundManager.setVolume(streamID, streamVolumes[buttonNumber])
            }

            streamVolumes[buttonNumber] = 0f
            soundManager.stop(streamID)
        }
    }

    private fun blend (fadeInStreamID: Int, fadeOutStreamID: Int, buttonNumber: Int) {

        thread {

            while (streamVolumes[buttonNumber] > 0 || blendInVolumes[buttonNumber] < MAX_VOLUME) {

                streamVolumes[buttonNumber] -= (MAX_VOLUME / (BLEND_TIME_MS / 10))
                blendInVolumes[buttonNumber] += (MAX_VOLUME / (BLEND_TIME_MS / 10))

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                soundManager.setVolume(fadeInStreamID, blendInVolumes[buttonNumber])
                soundManager.setVolume(fadeOutStreamID, streamVolumes[buttonNumber])
            }

            soundManager.setVolume(fadeOutStreamID, 0f)
            soundManager.stop(fadeOutStreamID)

            streamVolumes[buttonNumber] = MAX_VOLUME
            blendInVolumes[buttonNumber] = 0f
        }
    }

    private fun maxNonZeroIndex (arr: BooleanArray) : Int {

        var maxIndex : Int = -1

        for (i in arr.indices)
            if (arr[i]) maxIndex = i

        return maxIndex
    }
}