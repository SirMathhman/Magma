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
  assertValid("let x : I32 = 0I32;", "#include <stdint>\nint32_t x = 0;");
  assertValid("let y : I32 = 42;", "#include <stdint>\nint32_t y = 42;");
  assertValid("let a : I8 = 1;", "#include <stdint>\nint8_t a = 1;");
  assertValid("let b : I16 = 2;", "#include <stdint>\nint16_t b = 2;");
  assertValid("let c : I64 = 3;", "#include <stdint>\nint64_t c = 3;");
  assertValid("let d : U8 = 4;", "#include <stdint>\nuint8_t d = 4;");
  assertValid("let e : U16 = 5;", "#include <stdint>\nuint16_t e = 5;");
  assertValid("let f : U32 = 6;", "#include <stdint>\nuint32_t f = 6;");
  assertValid("let g : U64 = 7;", "#include <stdint>\nuint64_t g = 7;");
  assertInvalid("error");
  assertInvalid("let x : I32 = 0U64;");
  return 0;
}
