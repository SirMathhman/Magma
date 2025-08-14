#include "alwaysThrows.h"

Result alwaysThrows(const std::string& input) {
    return Error{"This function always throws an error."};
}
