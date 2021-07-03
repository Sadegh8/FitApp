package com.panda.app.fitapp.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.panda.app.fitapp.MainActivity
import com.panda.app.fitapp.R


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setTheme(R.style.splashScreenTheme)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({

            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

        }, 2000)


    }
}
