package com.example.hacker.phonecontrol.volume

import android.media.AudioManager

/** Volume controller for device audio */
class VolumeController(private val audioManager: AudioManager) {

    /** Get current volume */
    fun getCurrentVolume(): Int {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    /** Set volume to a specific level */
    fun setVolume(level: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0)
    }

    /** Increase volume */
    fun increaseVolume() {
        audioManager.adjustVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
    }

    /** Decrease volume */
    fun decreaseVolume() {
        audioManager.adjustVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
    }
}