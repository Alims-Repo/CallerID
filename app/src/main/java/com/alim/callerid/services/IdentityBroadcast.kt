package com.alim.callerid.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.alim.callerid.config.Dummey.predefinedIdentities
import com.alim.callerid.databinding.DialogCallerInfoBinding

class IdentityBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("IdentityBroadcast", "onReceive: ${intent.action}")
        if (intent.action == "com.alim.callerid.SHOW_OVERLAY") {
            val phoneNumber = intent.getStringExtra("phone_number")
            showCallerInfo(context, phoneNumber ?: "Unknown")
        }
    }

    private fun showCallerInfo(context: Context, phoneNumber: String) {
        val binding = DialogCallerInfoBinding.inflate(LayoutInflater.from(context))

        binding.name = predefinedIdentities[phoneNumber] ?: "Unknown"
        binding.number = phoneNumber

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = getLayoutParam(Gravity.CENTER)

        binding.root.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Record initial positions
                        initialX = layoutParams.x
                        initialY = layoutParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Update layout params to move the view
                        layoutParams.x = (initialX + (event.rawX - initialTouchX)).toInt()
                        layoutParams.y = (initialY + (event.rawY - initialTouchY)).toInt()
                        windowManager.updateViewLayout(binding.root, layoutParams)
                        return true
                    }
                }
                return false
            }
        })

        windowManager.run {
            addView(binding.root, layoutParams)

            binding.close.setOnClickListener {
                removeView(binding.root)
            }
        }
    }

    private fun getLayoutParam(gravity: Int): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).also {
            it.gravity = gravity
            it.x = 0
            it.y = 0
        }
    }
}