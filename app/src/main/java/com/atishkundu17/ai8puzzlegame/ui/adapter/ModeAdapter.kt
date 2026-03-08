package com.atishkundu17.ai8puzzlegame.ui.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atishkundu17.ai8puzzlegame.R
import com.atishkundu17.ai8puzzlegame.databinding.ItemModeBinding
import com.atishkundu17.ai8puzzlegame.model.Mode

class ModeAdapter(
    private val modes: List<Mode>,
    private val onClick: (Mode) -> Unit
) : RecyclerView.Adapter<ModeAdapter.ModeViewHolder>() {

    private var selectedPosition = 0

    inner class ModeViewHolder(
        val binding: ItemModeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModeViewHolder {

        val binding = ItemModeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ModeViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ModeViewHolder,
        position: Int
    ) {

        val mode = modes[position]
        val isSelected = position == selectedPosition

        holder.binding.modeName.text = mode.name

        holder.binding.modeCard.apply {

            if (isSelected) {
                setCardBackgroundColor(
                    Color.parseColor("#33BB86FC") // transparent purple
                )
                strokeWidth = 3
                strokeColor = Color.parseColor("#BB86FC")
                animate()
                    .scaleX(if (isSelected) 1.05f else 1f)
                    .scaleY(if (isSelected) 1.05f else 1f)
                    .setDuration(180)
                    .start()
                alpha = 1f
            } else {
                setCardBackgroundColor(
                    Color.parseColor("#1FFFFFFF") // subtle glass white
                )
                strokeWidth = 0
                scaleX = 1f
                scaleY = 1f
                alpha = 0.85f
            }
        }

        holder.binding.modeCard.setOnClickListener {

            val previousPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            onClick(mode)
        }

        createMiniBoard(holder.binding.miniBoard, mode.goal)
    }

    override fun getItemCount(): Int {
        return modes.size
    }

    private fun createMiniBoard(
        grid: GridLayout,
        goal: IntArray
    ) {

        grid.removeAllViews()

        for (i in 0..8) {

            val textView = TextView(grid.context)

            if (goal[i] == 0) {
                textView.visibility = View.INVISIBLE
            } else {
                textView.text = goal[i].toString()
                textView.setBackgroundResource(R.drawable.tile_background)
            }
            
            textView.setTextColor(Color.WHITE)
            textView.gravity = Gravity.CENTER
            textView.textSize = 14f

            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 0
            params.rowSpec = GridLayout.spec(i / 3, 1f)
            params.columnSpec = GridLayout.spec(i % 3, 1f)
            params.setMargins(4, 4, 4, 4)

            textView.layoutParams = params

            grid.addView(textView)
        }
    }
}