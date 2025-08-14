#include "Result.h"
#include "alwaysThrows.h"
#include <iostream>

int main() {
    Result result = alwaysThrows("test input");
    if (std::holds_alternative<Error>(result)) {
        std::cout << "Error: " << std::get<Error>(result).message << std::endl;
        return 0;
    } else {
        std::cout << "Unexpected success: " << std::get<std::string>(result) << std::endl;
        return 1;
    }
}
