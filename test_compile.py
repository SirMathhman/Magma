def test_compile_type_annotation_int_valid():
    input_code = "let x : I32 = 123;"
    output = compile(input_code)
    lines = output.splitlines()
    c_type = lines[1].split()[0]
    assert c_type == "int32_t"


def test_compile_type_annotation_int_invalid():
    with pytest.raises(
        Exception, match="Type annotation 'I32' does not match inferred type 'U32'."
    ):
        compile("let x : I32 = 123U32;")


def test_compile_type_annotation_float_valid():
    input_code = "let f : F32 = 1.0;"
    output = compile(input_code)
    c_type = output.split()[0]
    assert c_type == "float"


def test_compile_type_annotation_float_invalid():
    with pytest.raises(
        Exception, match="Type annotation 'F32' does not match inferred type 'F64'."
    ):
        compile("let f : F32 = 1.0F64;")


def test_compile_type_annotation_bool_valid():
    input_code = "let b : Bool = true;"
    output = compile(input_code)
    lines = output.splitlines()
    c_type = lines[1].split()[0]
    assert c_type == "bool"


def test_compile_type_annotation_bool_invalid():
    with pytest.raises(
        Exception, match="Type annotation 'Bool' does not match inferred type 'None'."
    ):
        compile("let b : Bool = 1;")


def test_compile_default_float_type():
    input_code = "let f = 0.0;"
    output = compile(input_code)
    c_type = output.split()[0]
    assert c_type == "float"


def test_compile_literal_f32_suffix():
    input_code = "let x = 0.0F32;"
    output = compile(input_code)
    c_type = output.split()[0]
    assert c_type == "float"


def test_compile_literal_f64_suffix():
    input_code = "let x = 1.5F64;"
    output = compile(input_code)
    c_type = output.split()[0]
    assert c_type == "double"


def test_compile_type_annotation_and_literal_suffix_mismatch():
    with pytest.raises(
        Exception, match="Type annotation 'I16' does not match inferred type 'U32'."
    ):
        compile("let x : I16 = 0U32;")


def test_compile_type_and_literal_suffix():
    input_code = "let x : I32 = 0I32;"
    output = compile(input_code)
    lines = output.splitlines()
    c_type = lines[1].split()[0]
    assert c_type == "int32_t"


def test_compile_literal_type_suffix():
    input_code = "let x = 0I32;"
    output = compile(input_code)
    lines = output.splitlines()
    c_type = lines[1].split()[0]
    assert c_type == "int32_t"


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
