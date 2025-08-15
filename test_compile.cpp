#include <iostream>
#include <string>
#include "compile.cpp"

void assertValid(const std::string &input, const std::string &expected)
{
  try
  {
    std::string result = compile(input);
    if (result == expected)
    {
      std::cout << "Test passed: input '" << input << "' returns expected output." << std::endl;
    }
    else
    {
      std::cerr << "Test failed: input '" << input << "' did not return expected output." << std::endl;
      exit(1);
    }
  }
  catch (...)
  {
    std::cerr << "Test failed: Exception thrown for valid input '" << input << "'." << std::endl;
    exit(1);
  }
}

void assertInvalid(const std::string &input)
{
  try
  {
    compile(input);
    std::cerr << "Test failed: No exception thrown for invalid input '" << input << "'." << std::endl;
    exit(1);
  }
  catch (const std::runtime_error &e)
  {
    std::cout << "Test passed: Exception caught for invalid input '" << input << "': " << e.what() << std::endl;
  }
  catch (...)
  {
    std::cerr << "Test failed: Unexpected exception type for invalid input '" << input << "'." << std::endl;
    exit(1);
  }
}

int main()
{
  assertValid("", "");
  assertValid("import stdexcept;", "#include <stdexcept>\n");
  assertValid("module std {}", "");
  assertValid("module foo {}", "");
  assertValid("let x : I32 = 100;", "#include <stdint>\nint32_t x = 100;");
  assertInvalid("error");
  return 0;
}
