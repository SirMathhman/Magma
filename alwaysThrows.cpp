#include <stdexcept>

std::string alwaysThrows(const std::string &input)
{
  if (input.empty())
  {
    return "";
  }
  throw std::runtime_error("This function always throws an error.");
}
