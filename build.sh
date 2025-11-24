#!/bin/bash
# Build script for Magma C++ project

set -e

BUILD_TYPE="${1:-Release}"
BUILD_DIR="build"
CLEAN=false
RUN=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN=true
            shift
            ;;
        --run)
            RUN=true
            shift
            ;;
        Debug|Release)
            BUILD_TYPE=$1
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [Debug|Release] [--clean] [--run]"
            exit 1
            ;;
    esac
done

# Clean build directory if requested
if [ "$CLEAN" = true ] && [ -d "$BUILD_DIR" ]; then
    echo "Cleaning build directory..."
    rm -rf "$BUILD_DIR"
fi

# Create build directory
mkdir -p "$BUILD_DIR"

# Configure and build
echo "Configuring CMake..."
cd "$BUILD_DIR"
cmake .. -DCMAKE_BUILD_TYPE="$BUILD_TYPE"

echo "Building project..."
cmake --build . --config "$BUILD_TYPE" -j$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)

echo ""
echo "Build successful!"
echo "Executable location: dist/windows/magma"

# Run if requested
if [ "$RUN" = true ]; then
    echo ""
    echo "Running magma..."
    cd ..
    ./dist/windows/magma
fi
