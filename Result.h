#pragma once
#include <string>
#include <variant>

struct Error {
    std::string message;
};

using Result = std::variant<std::string, Error>;
