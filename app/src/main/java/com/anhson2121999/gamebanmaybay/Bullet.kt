package com.anhson2121999.gamebanmaybay

import android.graphics.RectF

class Bullet(screenY: Int,
             private val speed: Float = 350f,
             heightModifier: Float = 20f) {

    val position = RectF()

    // Bắn lên hay bắn xuống
    val up = 0
    val down = 1

    // bắn đi đâu
    private var heading = -1

    private val width = 2
    private var height = screenY / heightModifier

    var isActive = false

    fun shoot(startX: Float, startY: Float, direction: Int): Boolean {
        if (!isActive) {
            position.left = startX
            position.top = startY
            position.right = position.left + width
            position.bottom = position.top + height
            heading = direction
            isActive = true
            return true
        }

        // Bullet already active
        return false
    }

    fun update(fps: Long) {

        // Bắn lên hay xuống
        if (heading == up) {
            position.top -= speed / fps
        } else {
            position.top += speed / fps
        }

        // Update vị trí
        position.bottom = position.top + height
    }
}