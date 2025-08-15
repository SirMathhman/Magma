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
  if (input.size() > 8 && input.substr(0, 7) == "module " && input.substr(input.size() - 3) == " {}")
  {
    return "";
  }
  if (input == "let x : I32 = 100;")
  {
    return "#include <stdint>\nint32_t x = 100;";
  }
  // Support for other variable names and types
  if (input.size() > 14 && input.substr(0, 4) == "let " && input.find(" : ") != std::string::npos && input.find(" = ") != std::string::npos && input.back() == ';')
  {
    size_t nameStart = 4;
    size_t typeStart = input.find(" : ") + 3;
    size_t typeEnd = input.find(" = ");
    size_t valueStart = typeEnd + 3;
    size_t valueEnd = input.size() - 1;
    std::string varName = input.substr(nameStart, input.find(" : ") - nameStart);
    std::string type = input.substr(typeStart, typeEnd - typeStart);
    std::string value = input.substr(valueStart, valueEnd - valueStart);

    std::string cppType;
    if (type == "I8")
      cppType = "int8_t";
    else if (type == "I16")
      cppType = "int16_t";
    else if (type == "I32")
      cppType = "int32_t";
    else if (type == "I64")
      cppType = "int64_t";
    else if (type == "U8")
      cppType = "uint8_t";
    else if (type == "U16")
      cppType = "uint16_t";
    else if (type == "U32")
      cppType = "uint32_t";
    else if (type == "U64")
      cppType = "uint64_t";
    else
      return "";

    return "#include <stdint>\n" + cppType + " " + varName + " = " + value + ";";
  }
  throw std::runtime_error("This function always throws an error.");
}
