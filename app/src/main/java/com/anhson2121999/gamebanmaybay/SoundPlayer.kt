package com.anhson2121999.gamebanmaybay

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException

class SoundPlayer(context: Context) {

    // For sound FX
    private val soundPool: SoundPool = SoundPool(10,
            AudioManager.STREAM_MUSIC,
            0)

    companion object {
        var playerExplodeID = -1
        var invaderExplodeID = -1
        var shootID = -1
        var damageShelterID = -1
        var uhID = -1
        var ohID = -1
    }

    init {
        try {
            // Tạo đối tượng
            val assetManager = context.assets
            var descriptor: AssetFileDescriptor


            // Tải âm thanh
            descriptor = assetManager.openFd("shoot.ogg")
            shootID = soundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("invaderexplode.ogg")
            invaderExplodeID = soundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("damageshelter.ogg")
            damageShelterID = soundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("playerexplode.ogg")
            playerExplodeID = soundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("damageshelter.ogg")
            damageShelterID = soundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("uh.ogg")
            uhID = soundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("oh.ogg")
            ohID = soundPool.load(descriptor, 0)


        } catch (e: IOException) {
            Log.e("error", "failed to load sound files")
        }
    }

    fun playSound(id: Int){
        soundPool.play(id, 1f, 1f, 0, 0, 1f)
    }
}