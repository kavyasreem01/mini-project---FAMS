package com.example.faams

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class splashpage : AppCompatActivity() {

    private val splashScreenDuration: Long = 3000 // 3 seconds
    private val TAG = "SplashPage"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashpage)

        Log.d(TAG, "Splash screen started")

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }, splashScreenDuration)

    }
}
