package com.kashithekash.violinesque.viewmodels

import com.kashithekash.violinesque.utility.SoundManagerStringBased
import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.utility.Config
import com.kashithekash.violinesque.utility.PrefRepo

class LayoutViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var soundManagerStringBased: SoundManagerStringBased

    private lateinit var prefRepo: PrefRepo

    val expandButtonsLiveData: MutableLiveData<Boolean> = MutableLiveData(Config.expandButtons)
    val handPositionsMutableList: MutableList<Int> = Config.handPositionsList.toMutableStateList()

    fun buttonTouched (buttonNumber: Int) {
        soundManagerStringBased.handleButtonTouch(buttonNumber)
    }

    fun buttonReleased (buttonNumber: Int) {
        soundManagerStringBased.handleButtonRelease(buttonNumber)
    }

    fun setSoundManager (soundManagerStringBasedInstance: SoundManagerStringBased) {
        soundManagerStringBased = soundManagerStringBasedInstance
    }

    fun setPrefRepo (prefRepoInstance: PrefRepo) {
        prefRepo = prefRepoInstance
    }

    fun setExpandButtons (newExpandButtons: Boolean) {
        expandButtonsLiveData.value = newExpandButtons
        Config.setExpandButtons(newExpandButtons)
        prefRepo.setExpandButtons(newExpandButtons)
    }

    fun addHandPosition (index: Int, newHandPosition: Int) {
        handPositionsMutableList.add(index, newHandPosition)
        Config.setHandPositionsList(handPositionsMutableList)
        prefRepo.setHandPositionsList(handPositionsMutableList)
    }

    fun removeHandPosition (index: Int) {
        handPositionsMutableList.removeAt(index)
        Config.setHandPositionsList(handPositionsMutableList)
        prefRepo.setHandPositionsList(handPositionsMutableList)
    }

    fun changeHandPosition (index: Int, newHandPosition: Int) {
        handPositionsMutableList[index] = newHandPosition
        Config.setHandPositionsList(handPositionsMutableList)
        prefRepo.setHandPositionsList(handPositionsMutableList)
    }

    fun handleHandPositionsListUpdate () {
        Config.setHandPositionsList(handPositionsMutableList)
        prefRepo.setHandPositionsList(handPositionsMutableList)
    }
}