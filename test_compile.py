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

def test_compile_let_x_u8():
    input_code = "let x : U8 = 0;"
    expected_output = "#include <stdint.h>\nuint8_t x = 0;"
    assert compile(input_code) == expected_output
