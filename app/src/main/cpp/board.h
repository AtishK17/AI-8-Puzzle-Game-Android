#ifndef BOARD_H
#define BOARD_H

#include <vector>
#include <string>

class Board {
public:
    std::vector<int> state;
    static std::vector<int> goal;

    Board();
    Board(std::vector<int> s);

    static void setGoal(const std::vector<int>& g);

    bool isGoal() const;
    int manhattan() const;
    std::vector<Board> getNeighbors() const;
    std::string serialize() const;
};

#endif