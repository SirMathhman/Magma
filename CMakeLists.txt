cmake_minimum_required(VERSION 3.17)
project(Magma C)

set(CMAKE_C_STANDARD 11)

add_executable(Magma Main.c Main.h Core.h Core.c Assert.c Assert.h AssertTest.c AssertTest.h)
add_executable(MagmaTest Test.c Test.h Core.h Core.c Assert.c Assert.h AssertTest.c AssertTest.h)