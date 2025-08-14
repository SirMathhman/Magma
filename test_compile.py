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
    "magma_type,expected_c_type",
    [
        ("U8", "uint8_t"),
        ("U16", "uint16_t"),
        ("U32", "uint32_t"),
        ("U64", "uint64_t"),
        ("I8", "int8_t"),
        ("I16", "int16_t"),
        ("I32", "int32_t"),
        ("I64", "int64_t"),
        ("Bool", "bool"),
    ],
)
def test_type_mapping(magma_type, expected_c_type):
    input_code = (
        f"let x : {magma_type} = 1;"
        if magma_type != "Bool"
        else f"let x : Bool = true;"
    )
    output = compile(input_code)
    # Extract the C type from the output
    c_type = output.split()[1]
    assert c_type == expected_c_type
