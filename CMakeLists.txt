cmake_minimum_required(VERSION 3.20)
project(Magma VERSION 1.0.0 LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Output directories
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/dist/windows)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/dist/windows/lib)
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/dist/windows/lib)

# Compiler-specific options
if(MSVC)
    # MSVC specific flags
    add_compile_options(/W4 /permissive-)
    # Enable big object files for large generated code
    add_compile_options(/bigobj)
else()
    # GCC/Clang flags
    add_compile_options(-Wall -Wextra -Wpedantic)
    # Enable optimization in Release
    if(CMAKE_BUILD_TYPE STREQUAL "Release")
        add_compile_options(-O3)
    endif()
endif()

# Source files
set(MAGMA_SOURCES
    src/main/windows/magma/Main.cpp
)

# Executable target
add_executable(magma ${MAGMA_SOURCES})

# Set target properties
set_target_properties(magma PROPERTIES
    OUTPUT_NAME "magma"
    CXX_STANDARD 20
    CXX_STANDARD_REQUIRED ON
)

# Platform-specific settings
if(WIN32)
    # Windows specific settings
    target_compile_definitions(magma PRIVATE _CRT_SECURE_NO_WARNINGS)
elseif(UNIX AND NOT APPLE)
    # Linux specific settings
    target_link_libraries(magma PRIVATE pthread)
elseif(APPLE)
    # macOS specific settings
    set_target_properties(magma PROPERTIES
        MACOSX_RPATH ON
    )
endif()

# Installation
install(TARGETS magma
    RUNTIME DESTINATION bin
    LIBRARY DESTINATION lib
    ARCHIVE DESTINATION lib
)

# Print build configuration
message(STATUS "=== Magma Build Configuration ===")
message(STATUS "Build type: ${CMAKE_BUILD_TYPE}")
message(STATUS "C++ standard: ${CMAKE_CXX_STANDARD}")
message(STATUS "Compiler: ${CMAKE_CXX_COMPILER_ID}")
message(STATUS "Source directory: ${CMAKE_SOURCE_DIR}")
message(STATUS "Binary directory: ${CMAKE_BINARY_DIR}")
message(STATUS "=================================")
