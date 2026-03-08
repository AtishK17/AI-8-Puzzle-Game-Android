package com.atishkundu17.ai8puzzlegame.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.atishkundu17.ai8puzzlegame.databinding.ActivitySetupBinding
import com.atishkundu17.ai8puzzlegame.model.Mode
import com.atishkundu17.ai8puzzlegame.ui.adapter.ModeAdapter

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private lateinit var selectedMode: Mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val modes = listOf(
            Mode("Standard Goal", intArrayOf(1,2,3,4,5,6,7,8,0)),
            Mode("Spiral Goal", intArrayOf(1,2,3,8,0,4,7,6,5)),
            Mode("Reverse Goal", intArrayOf(8,7,6,5,4,3,2,1,0))
        )

        selectedMode = modes[0]

        val adapter = ModeAdapter(modes) {
            selectedMode = it
        }

        binding.modeRecyclerView.layoutManager =
            LinearLayoutManager(this)

        binding.modeRecyclerView.adapter = adapter

        binding.startButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("GOAL", selectedMode.goal)
            intent.putExtra("MODE_NAME", selectedMode.name)
            startActivity(intent)
        }
    }
}