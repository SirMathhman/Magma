cmake_minimum_required(VERSION 3.17)
project(Magma C)

set(CMAKE_C_STANDARD 11)

add_executable(Magma main.c main.h)
add_executable(MagmaTest main.h test.c)