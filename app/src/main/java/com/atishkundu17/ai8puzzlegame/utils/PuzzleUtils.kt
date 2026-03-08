package com.atishkundu17.ai8puzzlegame.utils

object PuzzleUtils {

    fun generateRandomBoard(goal: IntArray): IntArray {

        val numbers = (0..8).toMutableList()

        do {
            numbers.shuffle()
        } while (!isSolvable(numbers.toIntArray(), goal))

        return numbers.toIntArray()
    }

    private fun inversionCount(arr: IntArray): Int {
        var inv = 0
        for (i in 0 until 8) {
            for (j in i + 1 until 9) {
                if (arr[i] != 0 &&
                    arr[j] != 0 &&
                    arr[i] > arr[j]) {
                    inv++
                }
            }
        }
        return inv
    }

    fun isSolvable(start: IntArray, goal: IntArray): Boolean {
        return inversionCount(start) % 2 ==
                inversionCount(goal) % 2
    }
}