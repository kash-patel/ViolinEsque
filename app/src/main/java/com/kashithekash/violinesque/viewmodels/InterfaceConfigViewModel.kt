package com.kashithekash.violinesque.viewmodels

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.utility.Config
import com.kashithekash.violinesque.utility.PrefRepo
import com.kashithekash.violinesque.utility.SoundManager

class InterfaceConfigViewModel (application: Application) : AndroidViewModel(application) {

//    private lateinit var soundManagerStringBased: SoundManagerStringBased
    private lateinit var soundManager: SoundManager

    private lateinit var prefRepo: PrefRepo

    val expandButtonsLiveData: MutableLiveData<Boolean> = MutableLiveData(Config.expandButtons)
    val alignButtonsToBottomLiveData: MutableLiveData<Boolean> = MutableLiveData(Config.alignButtonsToBottom)
    val handPositionsMutableList: MutableList<Int> = Config.handPositionsList.toMutableStateList()

    fun buttonTouched (buttonNumber: Int) {
        soundManager.handleButtonTouch(buttonNumber)
    }

    fun buttonReleased (buttonNumber: Int) {
        soundManager.handleButtonRelease(buttonNumber)
    }

    fun setSoundManager (soundManagerInstance: SoundManager) {
        soundManager = soundManagerInstance
    }

    fun setPrefRepo (prefRepoInstance: PrefRepo) {
        prefRepo = prefRepoInstance
    }

    fun setExpandButtons (newExpandButtons: Boolean) {
        expandButtonsLiveData.value = newExpandButtons
        Config.setExpandButtons(newExpandButtons)
        prefRepo.setExpandButtons(newExpandButtons)
    }

    fun setAlignButtonsToBottom (newAlignButtonsToBottom: Boolean) {
        alignButtonsToBottomLiveData.value = newAlignButtonsToBottom
        Config.setAlignButtonsToBottom(newAlignButtonsToBottom)
        prefRepo.setAlignButtonsToBottom(newAlignButtonsToBottom)
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
}