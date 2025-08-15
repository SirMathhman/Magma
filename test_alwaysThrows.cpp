#include <iostream>
#include "alwaysThrows.cpp"

int main() {
    try {
        alwaysThrows();
        std::cout << "Test failed: No exception thrown." << std::endl;
        return 1;
    } catch (const std::runtime_error& e) {
        std::cout << "Test passed: Exception caught: " << e.what() << std::endl;
        return 0;
    } catch (...) {
        std::cout << "Test failed: Unexpected exception type." << std::endl;
        return 1;
    }
}
