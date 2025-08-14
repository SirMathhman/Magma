import pytest
from compile import compile

def test_compile_empty():
    assert compile("") == ""

def test_compile_error():
    with pytest.raises(Exception, match="This function always errors."):
        compile("abc")

def test_compile_let_x():
    input_code = "let x : I32 =  100;"
    expected_output = "#include <stdint.h>\nint32_t x = 100;"
    assert compile(input_code) == expected_output

def test_compile_let_y():
    input_code = "let y : I32 =  100;"
    expected_output = "#include <stdint.h>\nint32_t y = 100;"
    assert compile(input_code) == expected_output

def test_compile_let_z():
    input_code = "let z : I32 =  42;"
    expected_output = "#include <stdint.h>\nint32_t z = 42;"
    assert compile(input_code) == expected_output


import pytest

@pytest.mark.parametrize(
    "input_code,expected_output",
    [
        ("let x : U8 = 0;", "#include <stdint.h>\nuint8_t x = 0;"),
        ("let x : U16 = 1;", "#include <stdint.h>\nuint16_t x = 1;"),
        ("let x : U32 = 2;", "#include <stdint.h>\nuint32_t x = 2;"),
        ("let x : U64 = 3;", "#include <stdint.h>\nuint64_t x = 3;"),
        ("let x : I8 = 4;", "#include <stdint.h>\nint8_t x = 4;"),
        ("let x : I16 = 5;", "#include <stdint.h>\nint16_t x = 5;"),
        ("let x : I32 = 6;", "#include <stdint.h>\nint32_t x = 6;"),
        ("let x : I64 = 7;", "#include <stdint.h>\nint64_t x = 7;"),
    ]
)
def test_compile_types(input_code, expected_output):
    assert compile(input_code) == expected_output
