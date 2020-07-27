package com.anhson2121999.gamebanmaybay

import android.app.Activity
import android.graphics.Point
import android.os.Bundle

class KotlinInvadersActivity : Activity() {

    // kotlinInvadersView: Main View cua Game, logic game, event touch
    private var kotlinInvadersView: KotlinInvadersView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val display = windowManager.defaultDisplay
        // Lấy độ phân giải
        val size = Point()
        display.getSize(size)

        // Khởi tạo màn hình game
        kotlinInvadersView = KotlinInvadersView(this, size)
        setContentView(kotlinInvadersView)
    }

    // Bắt đầu trò chơi
    override fun onResume() {
        super.onResume()

        // TIếp tục game
        kotlinInvadersView?.resume()
    }

    // Khi thoát game
    override fun onPause() {
        super.onPause()

        // Tạm dừng game
        kotlinInvadersView?.pause()
    }
}
