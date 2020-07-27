package com.anhson2121999.gamebanmaybay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.BitmapFactory
import com.anhson2121999.gamebanmaybay.R

class PlayerShip(context: Context,
                 private val screenX: Int,
                 screenY: Int) {

    // Bitmap đại diện máy bay
    var bitmap: Bitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.playership)

    // Size máy bay
    val width = screenX / 10f
    private val height = screenY / 10f

    // Vị trí tàu hiện tại
    val position = RectF(
            screenX / 2f,
            screenY-height,
            screenX/2 + width,
            screenY.toFloat())

    // Giữ tốc độ tàu di chuyển
    private val speed  = 450f

    //Dữ liệu truy cập Class.property
    companion object {
        // Tàu di chuyển
        const val stopped = 0
        const val left = 1
        const val right = 2
    }

    // Check tàu chuyern động
    var moving = stopped

    init{
        // stretch the bitmap to a size
        // appropriate for the screen resolution
        bitmap = Bitmap.createScaledBitmap(bitmap,
                width.toInt() ,
                height.toInt() ,
                false)
    }

    // Ng chơi di chuyển
    fun update(fps: Long) {
        // Di chuyển trong khu vực màn hình
        if (moving == left && position.left > 0) {
            position.left -= speed / fps
        }

        else if (moving == right && position.left < screenX - width) {
            position.left += speed / fps
        }

        position.right = position.left + width
    }

}