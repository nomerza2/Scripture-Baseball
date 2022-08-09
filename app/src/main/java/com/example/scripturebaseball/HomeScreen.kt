package com.example.scripturebaseball

import android.content.Intent
import android.net.Uri
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class HomeScreen : AppCompatActivity() {
    private val easyGuessLimit = 5
    private val normalGuessLimit = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
    }

    fun startGame(guessLimit: Int) {
        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("GUESS_LIMIT", guessLimit)
        startActivity(intent)
    }

    fun onEasyClick(view: View) {
        startGame(easyGuessLimit)
    }

    fun onNormalClick(view: View) {
        startGame(normalGuessLimit)
    }

    fun onAboutClick(view: View) {
        var intent = Intent(this, About::class.java)
        startActivity(intent)
    }
}