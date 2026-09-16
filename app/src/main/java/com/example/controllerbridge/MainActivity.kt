package com.example.controllerbridge

import android.app.Activity
import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale
import kotlin.math.abs

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var device: TextView
    private lateinit var lastEvent: TextView
    private lateinit var sticks: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 36, 36, 36)
        }

        fun label(text: String, size: Float = 18f): TextView =
            TextView(this).apply {
                this.text = text
                textSize = size
                setPadding(0, 12, 0, 12)
            }

        status = label("● Waiting for controller", 20f)
        device = label("Controller: not detected", 16f)
        lastEvent = label("Last input: —", 18f)
        sticks = label("Left stick:  X 0.00   Y 0.00\nRight stick: X 0.00   Y 0.00", 18f)

        root.addView(status)
        root.addView(device)
        root.addView(lastEvent)
        root.addView(sticks)
        root.addView(label("Press buttons or move either stick. This first version only reads Android's standard game-controller events.", 15f))

        setContentView(root)
        refreshDevices()
    }

    override fun onResume() {
        super.onResume()
        refreshDevices()
    }

    private fun refreshDevices() {
        val devices = InputDevice.getDeviceIds()
            .mapNotNull { InputDevice.getDevice(it) }
            .filter { isGameController(it) }

        if (devices.isEmpty()) {
            status.text = "● Waiting for controller"
            device.text = "Controller: not detected"
            return
        }

        val d = devices.first()
        status.text = "● Controller detected"
        device.text = "Controller: ${d.name}\nDevice ID: ${d.id}\nSources: ${sourceNames(d.sources)}"
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.ACTION_UP) {
            val action = if (event.action == KeyEvent.ACTION_DOWN) "DOWN" else "UP"
            val name = keyName(event.keyCode)
            lastEvent.text = "Last input: $name $action"
            refreshDevices()
        }
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE &&
            (event.source and InputDevice.SOURCE_CLASS_JOYSTICK) != 0
        ) {
            val lx = event.getAxisValue(MotionEvent.AXIS_X)
            val ly = event.getAxisValue(MotionEvent.AXIS_Y)
            val rx = event.getAxisValue(MotionEvent.AXIS_Z)
            val ry = event.getAxisValue(MotionEvent.AXIS_RZ)
            sticks.text = String.format(
                Locale.US,
                "Left stick:  X %+.2f   Y %+.2f\nRight stick: X %+.2f   Y %+.2f",
                lx, ly, rx, ry
            )
            lastEvent.text = "Last input: analog movement"
            refreshDevices()
            return true
        }
        return super.dispatchGenericMotionEvent(event)
    }

    private fun isGameController(d: InputDevice): Boolean {
        val s = d.sources
        return (s and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
               (s and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
    }

    private fun sourceNames(sources: Int): String {
        val names = mutableListOf<String>()
        if ((sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) names += "GAMEPAD"
        if ((sources and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) names += "JOYSTICK"
        if ((sources and InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD) names += "DPAD"
        return names.joinToString(", ").ifEmpty { "unknown" }
    }

    private fun keyName(code: Int): String = when (code) {
        KeyEvent.KEYCODE_BUTTON_A -> "A / Cross"
        KeyEvent.KEYCODE_BUTTON_B -> "B / Circle"
        KeyEvent.KEYCODE_BUTTON_X -> "X / Square"
        KeyEvent.KEYCODE_BUTTON_Y -> "Y / Triangle"
        KeyEvent.KEYCODE_BUTTON_L1 -> "L1"
        KeyEvent.KEYCODE_BUTTON_R1 -> "R1"
        KeyEvent.KEYCODE_BUTTON_L2 -> "L2"
        KeyEvent.KEYCODE_BUTTON_R2 -> "R2"
        KeyEvent.KEYCODE_BUTTON_THUMBL -> "Left stick click"
        KeyEvent.KEYCODE_BUTTON_THUMBR -> "Right stick click"
        KeyEvent.KEYCODE_BUTTON_START -> "Start"
        KeyEvent.KEYCODE_BUTTON_SELECT -> "Select"
        KeyEvent.KEYCODE_DPAD_UP -> "D-pad Up"
        KeyEvent.KEYCODE_DPAD_DOWN -> "D-pad Down"
        KeyEvent.KEYCODE_DPAD_LEFT -> "D-pad Left"
        KeyEvent.KEYCODE_DPAD_RIGHT -> "D-pad Right"
        KeyEvent.KEYCODE_BUTTON_MODE -> "Mode"
        else -> "KeyCode $code"
    }
}
