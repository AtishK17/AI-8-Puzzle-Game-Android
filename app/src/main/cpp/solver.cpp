#include "solver.h"
#include <queue>
#include <unordered_set>
#include <unordered_map>
#include <algorithm>

struct Node {
    Board board;
    int g;
    int f;

    bool operator>(const Node& other) const {
        return f > other.f;
    }
};

std::vector<Board> Solver::solve(Board start) {

    std::priority_queue<Node,
            std::vector<Node>,
            std::greater<Node>> pq;

    std::unordered_set<std::string> visited;
    std::unordered_map<std::string, std::string> parent;
    std::unordered_map<std::string, Board> boardMap;

    pq.push({start, 0, start.manhattan()});
    boardMap[start.serialize()] = start;

    while (!pq.empty()) {

        Node current = pq.top();
        pq.pop();

        std::string key = current.board.serialize();

        if (visited.count(key)) continue;
        visited.insert(key);

        if (current.board.isGoal()) {

            std::vector<Board> path;

            while (parent.count(key)) {
                path.push_back(boardMap[key]);
                key = parent[key];
            }

            path.push_back(start);
            std::reverse(path.begin(), path.end());

            return path;
        }

        for (Board neighbor : current.board.getNeighbors()) {

            std::string nkey = neighbor.serialize();

            if (!visited.count(nkey)) {

                parent[nkey] = key;
                boardMap[nkey] = neighbor;

                int g = current.g + 1;
                int f = g + neighbor.manhattan();

                pq.push({neighbor, g, f});
            }
        }
    }

    return {};
}