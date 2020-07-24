package com.anhson2121999.gamebanmaybay

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startGame.setOnClickListener{
            val intent: Intent = Intent(this, KotlinInvadersActivity::class.java)
            startActivity(intent)
        }
    }
}