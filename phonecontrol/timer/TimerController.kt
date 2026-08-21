package com.example.hacker.phonecontrol.timer

import android.os.CountDownTimer

/** Timer controller with countdown */
class TimerController {
    private var countDownTimer: CountDownTimer? = null
    private var onTimerFinished: (() -> Unit)? = null
    private var onProgress: ((remainingMillis: Long) -> Unit)? = null

    /** Start timer with millis duration */
    fun start(durationMillis: Long, onFinished: () -> Unit, onProgress: (remainingMillis: Long) -> Unit) {
        this.onTimerFinished = onFinished
        this.onProgress = onProgress

        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onProgress?.invoke(millisUntilFinished)
            }

            override fun onFinish() {
                onTimerFinished?.invoke()
                countDownTimer = null
            }
        }
        countDownTimer?.start()
    }

    /** Cancel timer */
    fun cancel() {
        countDownTimer?.cancel()
        countDownTimer = null
    }
}