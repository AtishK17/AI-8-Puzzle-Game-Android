package com.atishkundu17.ai8puzzlegame.native

object NativeSolver {

    init {
        System.loadLibrary("native-lib")
    }

    external fun solvePuzzle(
        start: IntArray,
        goal: IntArray
    ): String
}