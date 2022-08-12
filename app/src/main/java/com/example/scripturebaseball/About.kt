package com.example.scripturebaseball

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

    fun onInviteClick(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.churchofjesuschrist.org/comeuntochrist"))
        startActivity(browserIntent)
    }

    fun onPrivacyClick(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/scripture-baseball-privacy/home"))
        startActivity(browserIntent)
    }
}