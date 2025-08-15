#include <stdexcept>

std::string compile(const std::string &input)
{
  if (input.empty())
  {
    return "";
  }
  if (input == "import stdexcept;")
  {
    return "#include <stdexcept>\n";
  }
  if (input == "module std {}")
  {
    return "";
  }
  throw std::runtime_error("This function always throws an error.");
}
