package com.kashithekash.violinesque.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kashithekash.violinesque.utility.Config
import com.kashithekash.violinesque.utility.PrefRepo
import com.kashithekash.violinesque.utility.SoundManagerStringBased

const val MinFadeInTime = 10
const val MaxFadeInTime = 1000
const val MinBlendTime = 10
const val MaxBlendTime = 100
const val MinFadeOutTime = 10
const val MaxFadeOutTime = 1000
const val MinFadeOutDelay = 0
const val MaxFadeOutDelay = 500

class AudioSettingsViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var soundManagerStringBased: SoundManagerStringBased
    private lateinit var prefRepo: PrefRepo

    fun setSoundManager (soundManagerStringBasedInstance: SoundManagerStringBased) {
        soundManagerStringBased = soundManagerStringBasedInstance
    }

    fun setPrefRepo (prefRepoInstance: PrefRepo) {
        prefRepo = prefRepoInstance
    }

    fun setFadeInTime (newFadeInTime: Int) {
        Config.setFadeInTime(newFadeInTime)
        prefRepo.setFadeInTime(newFadeInTime)
        soundManagerStringBased.setFadeInTime(newFadeInTime)
    }

    fun setBlendTime (newBlendTime: Int) {
        Config.setBlendTime(newBlendTime)
        prefRepo.setBlendTime(newBlendTime)
        soundManagerStringBased.setBlendTime(newBlendTime)
    }

    fun setFadeOutTime (newFadeOutTime: Int) {
        Config.setFadeOutTime(newFadeOutTime)
        prefRepo.setFadeOutTime(newFadeOutTime)
        soundManagerStringBased.setFadeOutTime(newFadeOutTime)
    }

    fun setFadeOutDelay (newFadeOutDelay: Int) {
        Config.setFadeOutDelay(newFadeOutDelay)
        prefRepo.setFadeOutDelay(newFadeOutDelay)
        soundManagerStringBased.setFadeOutDelay(newFadeOutDelay)
    }

    fun getFadeInTime (): Int {
        return Config.fadeInTime
    }

    fun getBlendTime (): Int {
        return Config.blendTime
    }

    fun getFadeOutTime (): Int {
        return Config.fadeOutTime
    }

    fun getFadeOutDelay (): Int {
        return Config.fadeOutDelay
    }
}