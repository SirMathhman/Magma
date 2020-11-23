cmake_minimum_required(VERSION 3.17)
project(Magma C)

set(CMAKE_C_STANDARD 11)

add_executable(Magma Main.c Main.h)
add_executable(MagmaTest Main.c Main.h Test.c Test.h)