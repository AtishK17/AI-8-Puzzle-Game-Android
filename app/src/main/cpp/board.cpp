#include "board.h"
#include <cmath>
#include <algorithm>
using namespace std;

vector<int> Board::goal = {1, 2, 3, 4, 5, 6, 7, 8, 0};

Board::Board() {
    state = vector<int>(9,0);
}

Board::Board(vector<int> s) {
    state = s;
}

void Board::setGoal(const std::vector<int>& g) {
    goal = g;
}

bool Board::isGoal() const {
    return (state == goal);
}

int Board::manhattan() const {
    int dist = 0;
    for(int i = 0; i < 9; i++) {
        if(state[i] == 0) continue;
        
        int val = state[i];

        int goalInd = find(goal.begin(), goal.end(), val) - goal.begin();
        
        int goalRow = goalInd / 3;
        int goalCol = goalInd % 3;
        
        int currRow = i / 3;
        int currCol = i % 3;
        
        dist += (abs(goalRow - currRow) + abs(goalCol - currCol));
    }
    
    return dist;
}

vector<Board> Board::getNeighbors() const {
    vector<Board> neighbours;

    int zeroPos = 0;
    for(int i = 0; i < 9; i++) {
        if(state[i] == 0) {
            zeroPos = i;
            break;
        }
    }

    int row = zeroPos / 3;
    int col = zeroPos % 3;

    int dr[4] = {-1, 1, 0, 0};
    int dc[4] = {0, 0, -1, 1};

    for(int i = 0; i < 4; i++) {
        int nr = row + dr[i];
        int nc = col + dc[i];

        if (nr >= 0 && nc >= 0 && nr < 3 && nc < 3) {
            vector<int> newState = state;
            swap(newState[row * 3 + col], newState[nr * 3 + nc]);
            neighbours.emplace_back(newState);
        }
    }
        return neighbours;
}

string Board::serialize() const {
    string s = "";
    for(int x : state) {
        s += to_string(x);
    }

    return s;
}