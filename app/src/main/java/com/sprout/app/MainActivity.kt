package com.sprout.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sprout.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardDoodle.setOnClickListener {
            startActivity(Intent(this, DoodleActivity::class.java))
        }

        binding.cardPhotoHunt.setOnClickListener {
            startActivity(Intent(this, PhotoHuntActivity::class.java))
        }
    }
}
