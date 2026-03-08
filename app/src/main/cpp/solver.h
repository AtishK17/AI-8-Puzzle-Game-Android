#ifndef SOLVER_H
#define SOLVER_H

#include "board.h"
#include <vector>

class Solver {
public:
    static std::vector<Board> solve(Board start);
};

#endif