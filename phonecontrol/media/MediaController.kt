package com.example.hacker.phonecontrol.media

import android.media.MediaPlayer
import android.net.Uri

/** Media playback controller */
class MediaController(private val context: android.content.Context) {
    private var mediaPlayer: MediaPlayer? = null

    /** Play a URI */
    fun play(uri: Uri) {
        mediaPlayer = MediaPlayer.create(context, uri)
        mediaPlayer?.start()
    }

    /** Pause playback */
    fun pause() {
        mediaPlayer?.pause()
    }

    /** Stop playback */
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer = null
    }

    /** Is playing */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}