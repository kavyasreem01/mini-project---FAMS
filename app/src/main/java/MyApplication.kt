// MyApplication.kt

package com.example.faams

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase (if needed)
        FirebaseApp.initializeApp(this)

        // Other initialization code can go here
    }
}
