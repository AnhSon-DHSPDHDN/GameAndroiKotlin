package com.anhson2121999.gamebanmaybay

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.view.SurfaceView
import android.util.Log
import android.view.MotionEvent

class KotlinInvadersView(context: Context,
                         private val size: Point)
    : SurfaceView(context),
        Runnable {

    // Âm thanh game
    private val soundPlayer = SoundPlayer(context)

    // Game chính
    private val gameThread = Thread(this)

    private var playing = false

    // tạm dừng
    private var paused = true

    // Canvas vẽ đối tượng
    private var canvas: Canvas = Canvas()
    private val paint: Paint = Paint()

    // Máy Bay
    private var playerShip: PlayerShip = PlayerShip(context, size.x, size.y)

    // Dối thủ
    private val invaders = ArrayList<Invader>()
    private var numInvaders = 0

    // Công trình thủ
    private val bricks = ArrayList<DefenceBrick>()
    private var numBricks: Int = 0

    // Đạn của người chơi
    private var playerBullet = Bullet(size.y, 1200f, 40f)

    // Đạn của địch
    private val invadersBullets = ArrayList<Bullet>()
    private var nextBullet = 0
    private val maxInvaderBullets = 10

    // Điểm
    private var score = 0

    // lớp đối thủ
    private var waves = 1

    // quân còn lại
    private var lives = 3

    // Ghi điểm cao nhất
    private val prefs: SharedPreferences = context.getSharedPreferences(
            "Kotlin Invaders",
            Context.MODE_PRIVATE)

    private var highScore =  prefs.getInt("highScore", 0)

    // khoảng cách
    private var menaceInterval: Long = 1000

    // âm thanh
    private var uhOrOh: Boolean = false
    // âm thanh lần trước
    private var lastMenaceTime = System.currentTimeMillis()




    private fun prepareLevel() {
        // Khởi tạo địch
        Invader.numberOfInvaders = 0
        numInvaders = 0
        for (column in 0..10) {
            for (row in 0..5) {
                invaders.add(Invader(context,
                        row,
                        column,
                        size.x,
                        size.y))

                numInvaders++
            }
        }

        // Xây tường thủ
        numBricks = 0
        for (shelterNumber in 0..4) {
            for (column in 0..18) {
                for (row in 0..8) {
                    bricks.add(DefenceBrick(row,
                            column,
                            shelterNumber,
                            size.x,
                            size.y))

                    numBricks++
                }
            }
        }

        // Khởi tạo đạn quân địch
        for (i in 0 until maxInvaderBullets) {
            invadersBullets.add(Bullet(size.y))
        }
    }

    override fun run() {
        // kiểm tra fps
        var fps: Long = 0

        while (playing) {

            // thời điêm hiện tại
            val startFrameTime = System.currentTimeMillis()

            // Cập nhật fps
            if (!paused) {
                update(fps)
            }

            // Vẽ ảnh mới
            draw()

            // tốc độ fps
            val timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame
            }

            // Âm thanh nguy kịch
            if (!paused && ((startFrameTime - lastMenaceTime) > menaceInterval))
                menacePlayer()
        }
    }

    private fun menacePlayer() {
        if (uhOrOh) {
            // Uh
            soundPlayer.playSound(SoundPlayer.uhID)

        } else {
            // Oh
            soundPlayer.playSound(SoundPlayer.ohID)
        }
        // đặt lại thời gian
        lastMenaceTime = System.currentTimeMillis()
        // cập nhật âm thanh
        uhOrOh = !uhOrOh

    }

    private fun update(fps: Long) {
        // Cập nhật trạng thái

        // di chuyển máy bay
        playerShip.update(fps)

        // Kiểm tra đối thủ va vào màn hình
        var bumped = false

        // Check THUA
        var lost = false

        // cập nhật kẻ thù
        for (invader in invaders) {

            if (invader.isVisible) {
                // kẻ thù di chuỷen
                invader.update(fps)

                if (invader.takeAim(playerShip.position.left,
                                playerShip.width,
                                waves)) {

                    // Bắn
                    if (invadersBullets[nextBullet].shoot(invader.position.left
                                    + invader.width / 2,
                                    invader.position.top, playerBullet.down)) {

                        // Chuẩn bị lần bắn tiếp
                        nextBullet++

                        // Lặp lại nếu Max đạn
                        if (nextBullet == maxInvaderBullets) {
                            // nếu hết địch dừng bắn.
                            nextBullet = 0
                        }
                    }
                }

                // Kiểm tra chạm màn hình
                if (invader.position.left > size.x - invader.width
                        || invader.position.left < 0) {

                    bumped = true

                }
            }
        }

        // Cập nhật đạn ng chơi
        if (playerBullet.isActive) {
            playerBullet.update(fps)
        }

        // Đạn địch

        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                bullet.update(fps)
            }
        }

        // Có 1 tàu địch chạm vào góc màn hình
        if (bumped) {

            // Chuyển hướng + dịch xuống
            for (invader in invaders) {
                invader.dropDownAndReverse(waves)
                // Địch đổ bộ thành công
                if (invader.position.bottom >= size.y && invader.isVisible) {
                    lost = true
                }
            }
        }

        // Has the player's playerBullet hit the top of the screen
        if (playerBullet.position.bottom < 0) {
            playerBullet.isActive =false
        }

        // Đạn chạm đỉnh màn hình
        for (bullet in invadersBullets) {
            if (bullet.position.top > size.y) {
                bullet.isActive = false
            }
        }

        // Đạn trúng địch
        if (playerBullet.isActive) {
            for (invader in invaders) {
                if (invader.isVisible) {
                    if (RectF.intersects(playerBullet.position, invader.position)) {
                        invader.isVisible = false

                        soundPlayer.playSound(SoundPlayer.invaderExplodeID)
                        playerBullet.isActive = false
                        Invader.numberOfInvaders --
                        score += 10
                        if(score > highScore){
                            highScore = score
                        }

                        // Hoàn thành màn chơi
                        //if (score == numInvaders * 10 * waves) {
                        if (Invader.numberOfInvaders == 0) {
                            paused = true
                            lives ++
                            invaders.clear()
                            bricks.clear()
                            invadersBullets.clear()
                            prepareLevel()
                            waves ++
                            break
                        }

                        // k kiểm tra địch nữa
                        break
                    }
                }
            }
        }

        // Đạn trúng tường
        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                for (brick in bricks) {
                    if (brick.isVisible) {
                        if (RectF.intersects(bullet.position, brick.position)) {
                            // Check va chạm
                            bullet.isActive = false
                            brick.isVisible = false
                            soundPlayer.playSound(SoundPlayer.damageShelterID)
                        }
                    }
                }
            }

        }

        // Đạn người chơi trúng tường
        if (playerBullet.isActive) {
            for (brick in bricks) {
                if (brick.isVisible) {
                    if (RectF.intersects(playerBullet.position, brick.position)) {
                        playerBullet.isActive = false
                        brick.isVisible = false
                        soundPlayer.playSound(SoundPlayer.damageShelterID)
                    }
                }
            }
        }

        // Đạn địch trúng tàu
        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                if (RectF.intersects(playerShip.position, bullet.position)) {
                    bullet.isActive = false
                    lives --
                    soundPlayer.playSound(SoundPlayer.playerExplodeID)

                    // Game over!!!
                    if (lives == 0) {
                        lost = true
                        break
                    }
                }
            }
        }

        if (lost) {
            paused = true
            lives = 3
            score = 0
            waves = 1
            invaders.clear()
            bricks.clear()
            invadersBullets.clear()
            prepareLevel()
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            // Khóa Canvas
            canvas = holder.lockCanvas()

            // Màu nền
            canvas.drawColor(Color.argb(255, 0, 0, 0))

            // Chọn màu
            paint.color = Color.argb(255, 0, 255, 0)

            // Vẽ đối tượng
            canvas.drawBitmap(playerShip.bitmap, playerShip.position.left,
                    playerShip.position.top
                    , paint)

            // Vẽ tàu địch
            for (invader in invaders) {
                if (invader.isVisible) {
                    if (uhOrOh) {
                        Invader.bitmap1?.let {
                            canvas.drawBitmap(
                                it,
                                invader.position.left,
                                invader.position.top,
                                paint)
                        }
                    } else {
                        Invader.bitmap2?.let {
                            canvas.drawBitmap(
                                it,
                                invader.position.left,
                                invader.position.top,
                                paint)
                        }
                    }
                }
            }

            // Vẽ tường
            for (brick in bricks) {
                if (brick.isVisible) {
                    canvas.drawRect(brick.position, paint)
                }
            }

            // Đạn ng chơi
            if (playerBullet.isActive) {
                canvas.drawRect(playerBullet.position, paint)
            }

            // Đạn địch
            for (bullet in invadersBullets) {
                if (bullet.isActive) {
                    canvas.drawRect(bullet.position, paint)
                }
            }


            // Text
            paint.color = Color.argb(100, 255, 0, 0)
            paint.textSize = 50f
            canvas.drawText("Điểm: $score   HP: $lives Màn chơi: " +
                    "$waves Điểm cao: $highScore", 20f, 75f, paint)

            // Vẽ mọi thứ
            holder.unlockCanvasAndPost(canvas)
        }
    }


    fun pause() {
        playing = false
        try {
            gameThread.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }

        val prefs = context.getSharedPreferences(
                "Kotlin Invaders",
                Context.MODE_PRIVATE)

        val oldHighScore = prefs.getInt("highScore", 0)

        if(highScore > oldHighScore) {
            val editor = prefs.edit()

            editor.putInt(
                    "highScore", highScore)

            editor.apply()
        }
    }



    fun resume() {
        playing = true
        prepareLevel()
        gameThread.start()
    }

    //SurfaceView class implements onTouchListener
    // Phát hiện chạm màn hình
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action and MotionEvent.ACTION_MASK) {

        // Chạm màn hình
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE-> {
                paused = false

                if (motionEvent.y > size.y - size.y / 8) {
                    if (motionEvent.x > size.x / 2) {
                        playerShip.moving = PlayerShip.right
                    } else {
                        playerShip.moving = PlayerShip.left
                    }

                }

                if (motionEvent.y < size.y - size.y / 8) {
                    // Shots fired
                    if (playerBullet.shoot(
                                    playerShip.position.left + playerShip.width / 2f,
                                    playerShip.position.top,
                                    playerBullet.up)) {

                        soundPlayer.playSound(SoundPlayer.shootID)
                    }
                }
            }

        // Thả tay ra màn hình
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP -> {
                if (motionEvent.y > size.y - size.y / 10) {
                    playerShip.moving = PlayerShip.stopped
                }
            }

        }
        return true
    }

}