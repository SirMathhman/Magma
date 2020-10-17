cmake_minimum_required(VERSION 3.17)
project(Magma C)

set(CMAKE_C_STANDARD 11)

add_executable(Magma app.c app.h main.c)
add_executable(MagmaTest app.c app.h test.c)