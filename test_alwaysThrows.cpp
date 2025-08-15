#include <iostream>
#include <string>
#include "alwaysThrows.cpp"

int main()
{
  // Test with empty string
  try
  {
    std::string result = alwaysThrows("");
    if (result.empty())
    {
      std::cout << "Test passed: Empty string input returns empty string." << std::endl;
    }
    else
    {
      std::cout << "Test failed: Empty string input did not return empty string." << std::endl;
      return 1;
    }
  }
  catch (...)
  {
    std::cout << "Test failed: Exception thrown for empty string input." << std::endl;
    return 1;
  }

  // Test with 'import stdexcept;' input
  try
  {
    std::string result = alwaysThrows("import stdexcept;");
    if (result == "#include <stdexcept>\n")
    {
      std::cout << "Test passed: 'import stdexcept;' input returns correct include." << std::endl;
    }
    else
    {
      std::cout << "Test failed: 'import stdexcept;' input did not return correct include." << std::endl;
      return 1;
    }
  }
  catch (...)
  {
    std::cout << "Test failed: Exception thrown for 'import stdexcept;' input." << std::endl;
    return 1;
  }

  // Test with non-empty string
  try
  {
    alwaysThrows("error");
    std::cout << "Test failed: No exception thrown for non-empty string." << std::endl;
    return 1;
  }
  catch (const std::runtime_error &e)
  {
    std::cout << "Test passed: Exception caught for non-empty string: " << e.what() << std::endl;
    return 0;
  }
  catch (...)
  {
    std::cout << "Test failed: Unexpected exception type for non-empty string." << std::endl;
    return 1;
  }
}
