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
      std::cout << "Test failed: input '" << input << "' did not return expected output." << std::endl;
      exit(1);
    }
  }
  catch (...)
  {
    std::cout << "Test failed: Exception thrown for valid input '" << input << "'." << std::endl;
    exit(1);
  }
}

void assertInvalid(const std::string &input)
{
  try
  {
    compile(input);
    std::cout << "Test failed: No exception thrown for invalid input '" << input << "'." << std::endl;
    exit(1);
  }
  catch (const std::runtime_error &e)
  {
    std::cout << "Test passed: Exception caught for invalid input '" << input << "': " << e.what() << std::endl;
  }
  catch (...)
  {
    std::cout << "Test failed: Unexpected exception type for invalid input '" << input << "'." << std::endl;
    exit(1);
  }
}

int main()
{
  // Test with empty string
  assertValid("", "");

  // Test with 'import stdexcept;' input
  assertValid("import stdexcept;", "#include <stdexcept>\n");

  // Test with non-empty string
  assertInvalid("error");

  return 0;
}
