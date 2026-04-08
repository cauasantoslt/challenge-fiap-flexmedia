package com.fiap.totemflexmedia.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fiap.totemflexmedia.R

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val timeout = Runnable { resetToStandby() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemBars()
        resetToStandby()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        handler.removeCallbacks(timeout)
        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is StandbyFragment) {
            handler.postDelayed(timeout, 30000)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun resetToStandby() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, StandbyFragment()).commit()
    }
}