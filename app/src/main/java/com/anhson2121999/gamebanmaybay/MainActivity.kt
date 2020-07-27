package com.anhson2121999.gamebanmaybay

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        startGame.setOnClickListener{
            val intent: Intent = Intent(this, KotlinInvadersActivity::class.java)
            startActivity(intent)
        }
    }
}