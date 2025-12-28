package com.example.digitallearndiary.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.digitallearndiary.model.SensorEvent as AppSensorEvent
import android.content.Intent
import com.example.digitallearndiary.receiver.ConnectivityBroadcastReceiver

import kotlin.math.abs

class MotionSensorManager(
    private val context: Context,
    private val onEventDetected: (AppSensorEvent) -> Unit
) : SensorEventListener {

    private var lastEventTime = 0L
    private val COOLDOWN_MS = 5000L
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Cihazın hareket miktarını hesapla
        val movement = abs(x) + abs(y) + abs(z) - SensorManager.GRAVITY_EARTH
        val now = System.currentTimeMillis()

        if (movement > 2.5 && now - lastEventTime > COOLDOWN_MS) {
            lastEventTime = now
            onEventDetected(AppSensorEvent("FOCUS_LOST", "MOTION", now))
            val intent = Intent(context, ConnectivityBroadcastReceiver::class.java)
            intent.putExtra("type", "FOCUS_LOST")
            intent.putExtra("source", "MOTION")
            context.sendBroadcast(intent)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}