package com.example.hacker.phonecontrol.torch

import android.hardware.camera2.*
import android.view.SurfaceView

/** Torch/flashlight controller */
class TorchController(private val cameraManager: CameraManager?, private val cameraId: String) {

    /** Turn torch on */
    fun turnOn(): Boolean {
        try {
            cameraManager?.setTorchMode(cameraId, true)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /** Turn torch off */
    fun turnOff(): Boolean {
        try {
            cameraManager?.setTorchMode(cameraId, false)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /** Check if torch is available */
    fun isTorchAvailable(): Boolean {
        try {
            cameraManager?.isTorchAvailable(cameraId) ?: false
        } catch (e: Exception) {
            return false
        }
    }
}