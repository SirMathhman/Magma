cmake_minimum_required(VERSION 3.16)
project(MagmaTest LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_COMPILER "clang++")

add_executable(test_alwaysThrows 
    alwaysThrows.cpp
    test_alwaysThrows.cpp
)
