package com.atishkundu17.ai8puzzlegame.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.atishkundu17.ai8puzzlegame.R
import com.atishkundu17.ai8puzzlegame.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        animateTitle()
        createMiniPuzzle()
        animateSignature()

        lifecycleScope.launch {
            delay(3000)
            startActivity(Intent(this@SplashActivity, SetupActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun animateTitle() {
        binding.gameTitle.animate()
            .alpha(1f)
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(800)
            .start()
    }

    private fun animateSignature() {
        binding.poweredBy.animate()
            .alpha(1f)
            .setStartDelay(1200)
            .setDuration(800)
            .start()
    }

    private fun createMiniPuzzle() {

        val grid = binding.miniBoard
        val numbers = listOf(1,2,3,4,5,6,7,8,0)

        for (i in numbers.indices) {

            val tile = TextView(this)

            tile.text = if (numbers[i] == 0) "" else numbers[i].toString()
            tile.setTextColor(Color.WHITE)
            tile.gravity = Gravity.CENTER
            tile.textSize = 16f
            tile.setBackgroundResource(R.drawable.tile_background)
            tile.alpha = 0f

            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 0
            params.rowSpec = GridLayout.spec(i / 3, 1f)
            params.columnSpec = GridLayout.spec(i % 3, 1f)
            params.setMargins(6,6,6,6)

            tile.layoutParams = params
            grid.addView(tile)

            tile.animate()
                .alpha(1f)
                .setStartDelay((i * 120).toLong())
                .setDuration(400)
                .start()
        }
    }
}