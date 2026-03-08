cmake_minimum_required(VERSION 3.22.1)

project("ai8puzzlegame")

add_library(
        native-lib
        SHARED
        native-lib.cpp
        board.cpp
        solver.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        native-lib
        ${log-lib})