package com.example.wallpaperapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.wallpaperapplication.databinding.ActivitySplashBinding
import com.example.wallpaperapplication.main.MainScreenActivity
import com.example.wallpaperapplication.one_off_screens.OneOffScreenData
import com.example.wallpaperapplication.preferences.PreferenceManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding = ActivitySplashBinding.inflate(layoutInflater)


        setContentView(binding.root)


        preferenceManager = PreferenceManager(this@SplashActivity)

        val thread: Thread = object : Thread() {
            override fun run() {
                super.run()
                try {

                    binding.logo.startAnimation(AnimationUtils.loadAnimation(this@SplashActivity, R.anim.animation))

                    sleep(4000)

                    binding.logo.clearAnimation()

                } catch (e: Exception) {
                    e.printStackTrace()

                } finally {
                    //first run
                    if (preferenceManager.get_FirstRun()) {
                        // here run your first-time instructions, for example :
                        val intent = Intent(this@SplashActivity, OneOffScreenData::class.java)
                        startActivity(intent)
                        finish()

                        preferenceManager.set_FirstRun(firstRun = false)

                    } else {
                        val intent = Intent(this@SplashActivity, MainScreenActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        thread.start()

    }
}