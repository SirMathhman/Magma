def test_type_mapping_f32():
    input_code = "let x : F32 = 0.0;"
    output = compile(input_code)
    c_type = output.split()[0]
    assert c_type == "float"


def test_type_mapping_f64():
    input_code = "let x : F64 = 1.5;"
    output = compile(input_code)
    c_type = output.split()[0]
    assert c_type == "double"


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
    ],
)
def test_type_mapping(magma_type, expected_c_type):
    input_code = f"let x : {magma_type} = 1;"
    output = compile(input_code)
    lines = output.splitlines()
    # For int/uint types, type is first word on second line
    if magma_type.startswith("U") or magma_type.startswith("I"):
        c_type = lines[1].split()[0]
    else:
        c_type = lines[0].split()[0]
    assert c_type == expected_c_type


def test_type_mapping_bool_true():
    input_code = "let x : Bool = true;"
    output = compile(input_code)
    lines = output.splitlines()
    c_type = lines[1].split()[0]
    assert c_type == "bool"


def test_type_mapping_bool_false():
    input_code = "let x : Bool = false;"
    output = compile(input_code)
    lines = output.splitlines()
    c_type = lines[1].split()[0]
    assert c_type == "bool"
