package com.atishkundu17.ai8puzzlegame.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.atishkundu17.ai8puzzlegame.databinding.ActivityGameBinding
import com.atishkundu17.ai8puzzlegame.native.NativeSolver
import com.atishkundu17.ai8puzzlegame.utils.PuzzleUtils
import kotlin.math.abs
import androidx.lifecycle.lifecycleScope
import com.atishkundu17.ai8puzzlegame.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var selectedGoal: IntArray
    private var currentBoard = IntArray(9)
    private var moveCount = 0
    private var optimalMovesFromStart = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val modeName = intent.getStringExtra("MODE_NAME") ?: "Standard"

        selectedGoal = intent.getIntArrayExtra("GOAL")
            ?: intArrayOf(1,2,3,4,5,6,7,8,0)

        binding.goalText.text = modeName

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.goalModeTitle.text = modeName
        displayGoalPreview()

        binding.startOverButton.setOnClickListener {
            startGame()
            setTilesEnabled(true)
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        startGame()
        setupTiles()
    }

    private fun startGame() {

        currentBoard = PuzzleUtils.generateRandomBoard(selectedGoal)
        moveCount = 0

        updateGrid()

        binding.playerMoves.text = "0"
        binding.bestMoves.text = "..."
        binding.minRemaining.text = "..."

        lifecycleScope.launch {

            val result = withContext(Dispatchers.Default) {
                NativeSolver.solvePuzzle(currentBoard, selectedGoal)
            }

            optimalMovesFromStart = result.toInt()

            binding.bestMoves.text = optimalMovesFromStart.toString()
            binding.minRemaining.text = optimalMovesFromStart.toString()
        }
    }

    private fun setupTiles() {

        val buttons = listOf(
            binding.tile0, binding.tile1, binding.tile2,
            binding.tile3, binding.tile4, binding.tile5,
            binding.tile6, binding.tile7, binding.tile8
        )

        for (i in 0..8) {
            buttons[i].setOnTouchListener { view, event ->

                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        view.tag = Pair(event.rawX, event.rawY)
                    }

                    MotionEvent.ACTION_UP -> {

                        val (startX, startY) = view.tag as Pair<Float, Float>

                        val dx = event.rawX - startX
                        val dy = event.rawY - startY

                        handleSwipe(i, dx, dy)
                    }
                }
                true
            }
        }
    }

    private fun moveTile(index: Int) {

        val zeroIndex = currentBoard.indexOf(0)

        val row = index / 3
        val col = index % 3

        val zeroRow = zeroIndex / 3
        val zeroCol = zeroIndex % 3

        val isAdjacent =
            (row == zeroRow && abs(col - zeroCol) == 1) ||
                    (col == zeroCol && abs(row - zeroRow) == 1)

        if (!isAdjacent) return

        val buttons = listOf(
            binding.tile0, binding.tile1, binding.tile2,
            binding.tile3, binding.tile4, binding.tile5,
            binding.tile6, binding.tile7, binding.tile8
        )

        val clickedButton = buttons[index]
        val emptyButton = buttons[zeroIndex]

        val dx = emptyButton.x - clickedButton.x
        val dy = emptyButton.y - clickedButton.y

        clickedButton.animate()
            .translationX(dx)
            .translationY(dy)
            .setDuration(180)
            .withEndAction {

                // Reset animation offset
                clickedButton.translationX = 0f
                clickedButton.translationY = 0f

                // Swap board values
                currentBoard[zeroIndex] = currentBoard[index]
                currentBoard[index] = 0

                moveCount++
                binding.playerMoves.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(120)
                    .withEndAction {
                        binding.playerMoves.text = moveCount.toString()
                        binding.playerMoves.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(120)
                            .start()
                    }
                    .start()

                updateGrid()
                updateRemaining()

                if (currentBoard.contentEquals(selectedGoal)) {
                    onGameWon()
                }
            }
            .start()
    }

    private fun updateGrid() {

        val buttons = listOf(
            binding.tile0, binding.tile1, binding.tile2,
            binding.tile3, binding.tile4, binding.tile5,
            binding.tile6, binding.tile7, binding.tile8
        )

        for (i in 0..8) {

            if (currentBoard[i] == 0) {
                buttons[i].text = ""
                buttons[i].setBackgroundColor(android.graphics.Color.TRANSPARENT)
                buttons[i].elevation = 0f
                buttons[i].isClickable = false
            } else {
                buttons[i].text = currentBoard[i].toString()
                buttons[i].setBackgroundResource(R.drawable.tile_background)
                buttons[i].elevation = 8f
                buttons[i].isClickable = true
            }
        }
    }

    private fun updateRemaining() {

        lifecycleScope.launch {

            binding.minRemaining.text = "..."

            val result = withContext(Dispatchers.Default) {
                NativeSolver.solvePuzzle(currentBoard, selectedGoal)
            }

            binding.minRemaining.text = result.toInt().toString()
        }
    }

    private fun onGameWon() {

        setTilesEnabled(false)

        binding.startOverButton.isEnabled = false

        val dialogView = layoutInflater.inflate(R.layout.dialog_win, null)

        val performanceText =
            dialogView.findViewById<TextView>(R.id.performanceText)

        val restartButton =
            dialogView.findViewById<Button>(R.id.restartButton)

        val menuButton =
            dialogView.findViewById<Button>(R.id.menuButton)

        val performanceMessage = when {
            moveCount == optimalMovesFromStart ->
                "🏆 PERFECT\n\nYou solved it optimally in $moveCount moves."

            moveCount <= optimalMovesFromStart + 3 ->
                "🔥 GREAT JOB\n\nOnly ${moveCount - optimalMovesFromStart} above optimal."

            else ->
                "✅ COMPLETED\n\nYour Moves: $moveCount\nOptimal: $optimalMovesFromStart"
        }

        performanceText.text = performanceMessage

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        restartButton.setOnClickListener {
            dialog.dismiss()
            startGame()
            setTilesEnabled(true)
            binding.startOverButton.isEnabled = true
        }

        menuButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogView.scaleX = 0.8f
        dialogView.scaleY = 0.8f
        dialogView.alpha = 0f

        dialogView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(200)
            .start()

    }

    private fun setTilesEnabled(enabled: Boolean) {

        val buttons = listOf(
            binding.tile0, binding.tile1, binding.tile2,
            binding.tile3, binding.tile4, binding.tile5,
            binding.tile6, binding.tile7, binding.tile8
        )

        buttons.forEach { it.isEnabled = enabled }
    }

    private fun handleSwipe(index: Int, dx: Float, dy: Float) {

        val zeroIndex = currentBoard.indexOf(0)

        val row = index / 3
        val col = index % 3

        val zeroRow = zeroIndex / 3
        val zeroCol = zeroIndex % 3

        val threshold = 50f

        val horizontalSwipe = kotlin.math.abs(dx) > kotlin.math.abs(dy)

        if (horizontalSwipe) {

            if (dx > threshold && zeroRow == row && zeroCol == col + 1) {
                // Swiping RIGHT and empty is on RIGHT
                moveTile(index)
            }

            else if (dx < -threshold && zeroRow == row && zeroCol == col - 1) {
                // Swiping LEFT and empty is on LEFT
                moveTile(index)
            }

        } else {

            if (dy > threshold && zeroCol == col && zeroRow == row + 1) {
                // Swiping DOWN and empty is below
                moveTile(index)
            }

            else if (dy < -threshold && zeroCol == col && zeroRow == row - 1) {
                // Swiping UP and empty is above
                moveTile(index)
            }
        }
    }

    private fun displayGoalPreview() {

        val goalViews = listOf(
            binding.goal0, binding.goal1, binding.goal2,
            binding.goal3, binding.goal4, binding.goal5,
            binding.goal6, binding.goal7, binding.goal8
        )

        for (i in 0..8) {

            if (selectedGoal[i] == 0) {
                goalViews[i].text = ""
                goalViews[i].setBackgroundColor(android.graphics.Color.TRANSPARENT)
            } else {
                goalViews[i].text = selectedGoal[i].toString()
            }
        }
    }
}