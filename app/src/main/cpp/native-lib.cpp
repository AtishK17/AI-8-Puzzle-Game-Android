#include <jni.h>
#include <string>
#include <vector>
#include "solver.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_atishkundu17_ai8puzzlegame_native_NativeSolver_solvePuzzle(
        JNIEnv *env,
        jobject /* this */,
        jintArray startArray,
        jintArray goalArray) {

    // Convert Java arrays to C++ arrays
    jint *startArr = env->GetIntArrayElements(startArray, nullptr);
    jint *goalArr  = env->GetIntArrayElements(goalArray, nullptr);

    std::vector<int> startState(9);
    std::vector<int> goalState(9);

    for (int i = 0; i < 9; i++) {
        startState[i] = startArr[i];
        goalState[i]  = goalArr[i];
    }

    // Release memory
    env->ReleaseIntArrayElements(startArray, startArr, 0);
    env->ReleaseIntArrayElements(goalArray, goalArr, 0);

    // Set dynamic goal
    Board::setGoal(goalState);

    // Solve using A*
    Board start(startState);
    std::vector<Board> solution = Solver::solve(start);

    // If no solution found
    if (solution.empty()) {
        return env->NewStringUTF("0");
    }

    int moves = solution.size() - 1;

    std::string result = std::to_string(moves);

    return env->NewStringUTF(result.c_str());
}