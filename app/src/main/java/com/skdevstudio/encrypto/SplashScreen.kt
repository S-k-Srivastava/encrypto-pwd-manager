package com.skdevstudio.encrypto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.skdevstudio.encrypto.R

class SplashScreen : AppCompatActivity() {

    private var currentPin: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }, 400)

    }
}
