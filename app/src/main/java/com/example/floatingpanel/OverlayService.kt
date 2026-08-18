package com.example.floatingpanel

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var iconView: View? = null
    private var panelView: View? = null

    private val channelId = "nova_panel_channel"

    private val featureGroups: LinkedHashMap<String, List<String>> = linkedMapOf(
        "AIM" to listOf(
            "AIM BOT",
            "AIM LOCK",
            "AIM SMOOTH",
            "AIM FOV CIRCLE",
            "AUTO HEADSHOT",
            "BONE AIM",
            "AIM ASSIST+",
            "TARGET PRIORITY"
        ),
        "VISUAL / ESP" to listOf(
            "ESP PLAYER",
            "ESP LINE",
            "ESP BOX",
            "ESP SKELETON",
            "ESP HEALTH BAR",
            "ESP DISTANCE",
            "WALLHACK",
            "ITEM ESP"
        ),
        "MOVEMENT" to listOf(
            "SPEED HACK",
            "JUMP BOOST",
            "FLY MODE",
            "NO FALL DAMAGE",
            "TELEPORT",
            "ANTI SHAKE",
            "DRAG REDUCE",
            "GHOST MODE"
        ),
        "WEAPON" to listOf(
            "NO RECOIL",
            "NO SPREAD",
            "MAGIC BULLET",
            "FIRE RATE BOOST",
            "UNLIMITED AMMO",
            "INSTANT RELOAD",
            "BULLET TRACK",
            "ONE TAP"
        ),
        "SYSTEM" to listOf(
            "ANTI-BAN SHIELD",
            "NIGHT MODE",
            "HITBOX EXPAND",
            "RADAR HACK",
            "SPECTATOR ALERT",
            "FPS BOOST",
            "LOW GRAPHICS",
            "CONFIG SAVE"
        )
    )

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForegroundServiceNotification()
        addFloatingIcon()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundServiceNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Nova Panel", NotificationManager.IMPORTANCE_MIN
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(getString(R.string.notif_text))
            .setSmallIcon(android.R.drawable.presence_online)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun addFloatingIcon() {
        val inflater = LayoutInflater.from(this)
        iconView = inflater.inflate(R.layout.floating_icon, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 40
        params.y = 200

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isDragging = false

        iconView?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (kotlin.math.abs(dx) > 8 || kotlin.math.abs(dy) > 8) {
                        isDragging = true
                    }
                    params.x = initialX + dx
                    params.y = initialY + dy
                    windowManager.updateViewLayout(iconView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        v.performClick()
                        togglePanel()
                    }
                    true
                }
                else -> false
            }
        }

        windowManager.addView(iconView, params)
    }

    private fun togglePanel() {
        if (panelView != null) {
            removePanel()
        } else {
            showPanel()
        }
    }

    private fun showPanel() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.floating_menu, null)
        panelView = view

        val container = view.findViewById<LinearLayout>(R.id.featureContainer)
        for ((category, names) in featureGroups) {
            val header = inflater.inflate(R.layout.item_section_header, container, false)
            header.findViewById<TextView>(R.id.sectionLabel).text = category
            container.addView(header)

            for (name in names) {
                val row = inflater.inflate(R.layout.item_feature_row, container, false)
                val label = row.findViewById<TextView>(R.id.featureLabel)
                val switch = row.findViewById<Switch>(R.id.featureSwitch)
                label.text = name
                switch.setOnCheckedChangeListener { _, isChecked ->
                    val state = if (isChecked) "ON" else "OFF"
                    Toast.makeText(this, "$name: $state", Toast.LENGTH_SHORT).show()
                }
                container.addView(row)
            }
        }

        view.findViewById<View>(R.id.closeBtn).setOnClickListener {
            removePanel()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        windowManager.addView(panelView, params)
    }

    private fun removePanel() {
        panelView?.let {
            windowManager.removeView(it)
            panelView = null
        }
    }

    private fun overlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE
    }

    override fun onDestroy() {
        super.onDestroy()
        iconView?.let { windowManager.removeView(it) }
        removePanel()
    }
}
