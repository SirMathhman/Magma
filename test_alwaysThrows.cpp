#include <iostream>
#include <string>
#include "alwaysThrows.cpp"

void assertValid(bool condition, const std::string &successMsg, const std::string &failMsg)
{
  if (condition)
  {
    std::cout << "Test passed: " << successMsg << std::endl;
  }
  else
  {
    std::cout << "Test failed: " << failMsg << std::endl;
    exit(1);
  }
}

int main()
{
  // Test with empty string
  try
  {
    std::string result = alwaysThrows("");
    assertValid(result.empty(), "Empty string input returns empty string.", "Empty string input did not return empty string.");
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
    assertValid(result == "#include <stdexcept>\n", "'import stdexcept;' input returns correct include.", "'import stdexcept;' input did not return correct include.");
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
