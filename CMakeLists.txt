cmake_minimum_required(VERSION 3.17)
project(Magma C)

set(CMAKE_C_STANDARD 11)

add_executable(Magma assertion.c assertion.h array.c array.h app.c app.h main.c core.c core.h exception.c exception.h option.c option.h strings.c strings.h)
add_executable(MagmaTest assertion.c assertion.h array.c array.h app.c app.h test.c core.c core.h exception.c exception.h option.c option.h strings.c strings.h)