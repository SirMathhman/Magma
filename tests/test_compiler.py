import sys
from pathlib import Path
import pytest

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'src'))

from magma import Compiler


def test_compile_empty_input_creates_empty_main(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int main() {\n}\n"


def test_compile_non_empty_returns_placeholder(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("hello")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: hello"


def test_compile_simple_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo() => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {\n}\n"



def test_compile_multiple_functions(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo() => {}\nfn bar() => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {\n}\nvoid bar() {\n}\n"


def test_compile_explicit_void_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn empty(): Void => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void empty() {\n}\n"


def test_compile_bool_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn truth(): Bool => { return true; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int truth() {\n    return 1;\n}\n"


def test_compile_bool_false_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn lie(): Bool => { return false; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int lie() {\n    return 0;\n}\n"


def test_compile_function_with_extra_whitespace(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn   spaced  (  )  :  Void  =>  {   }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void spaced() {\n}\n"


def test_compile_bool_with_whitespace(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn ws(): Bool => {  return true;  }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int ws() {\n    return 1;\n}\n"


def test_compile_function_with_newlines(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn nl\n(\n)\n:\nVoid\n=>\n{\n}\n")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void nl() {\n}\n"


def test_compile_function_with_carriage_returns(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    content = "fn cr\r\n(\r\n)\r\n:\r\nVoid\r\n=>\r\n{\r\n}\r\n"
    input_file.write_text(content)
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void cr() {\n}\n"


@pytest.mark.parametrize(
    "magma_type,c_type",
    [
        ("U8", "unsigned char"),
        ("U16", "unsigned short"),
        ("U32", "unsigned int"),
        ("U64", "unsigned long long"),
        ("I8", "signed char"),
        ("I16", "short"),
        ("I32", "int"),
        ("I64", "long long"),
    ],
)
def test_compile_numeric_return(tmp_path, magma_type, c_type):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(f"fn num(): {magma_type} => {{ return 0; }}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == f"{c_type} num() {{\n    return 0;\n}}\n"


def test_compile_let_numeric(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo(): Void => { let myValue: I32 = 420; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {\n    int myValue = 420;\n}\n"


def test_compile_let_bool(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn flag(): Void => { let flag: Bool = true; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void flag() {\n    int flag = 1;\n}\n"


def test_compile_let_invalid_value(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn bad(): Void => { let nope: I32 = true; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn bad(): Void => { let nope: I32 = true; }"

def test_compile_let_array_numeric(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn arr(): Void => { let myArray: [I32; 3] = [1, 2, 3]; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void arr() {\n    int myArray[] = {1, 2, 3};\n}\n"


def test_compile_let_array_invalid_size(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn badarr(): Void => { let arr: [I32; 2] = [1, 2, 3]; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn badarr(): Void => { let arr: [I32; 2] = [1, 2, 3]; }"


def test_compile_let_infer_bool(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn infer(): Void => { let value = false; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void infer() {\n    int value = 0;\n}\n"


def test_compile_let_infer_numeric(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn infer(): Void => { let myInt = 100; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void infer() {\n    int myInt = 100;\n}\n"


def test_compile_let_declaration_no_value(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn decl(): Void => { let value: I16; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void decl() {\n    short value;\n}\n"


def test_compile_let_void_assignment_invalid(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn bad(): Void => { let nothing: Void = 0; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn bad(): Void => { let nothing: Void = 0; }"


def test_compile_assignment_mut_numeric(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn assign(): Void => { let mut x: I32 = 100; x = 200; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void assign() {\n    int x = 100;\n    x = 200;\n}\n"


def test_compile_assignment_mut_bool(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn assign(): Void => { let mut flag: Bool = true; flag = false; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void assign() {\n    int flag = 1;\n    flag = 0;\n}\n"


def test_compile_assignment_without_mut_invalid(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn bad(): Void => { let x: I32 = 100; x = 200; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn bad(): Void => { let x: I32 = 100; x = 200; }"


def test_compile_assignment_type_mismatch_invalid(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn bad(): Void => { let mut x: I32 = 100; x = true; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn bad(): Void => { let mut x: I32 = 100; x = true; }"

def test_compile_struct_simple(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("struct Point {x : I32; y : I32}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "struct Point {\n    int x;\n    int y;\n};\n"


def test_compile_struct_bool_field(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("struct Flag {value : Bool}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "struct Flag {\n    int value;\n};\n"


def test_compile_struct_invalid_type(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("struct Bad {x : Unknown}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: struct Bad {x : Unknown}"


def test_compile_enum_simple(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("enum MyEnum { First, Second }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "enum MyEnum { First, Second };\n"


def test_compile_function_with_numeric_params(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn add(x: I32, y: I32): I32 => { return 0; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int add(int x, int y) {\n    return 0;\n}\n"


def test_compile_function_with_bool_param(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn flag(value: Bool): Bool => { return true; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int flag(int value) {\n    return 1;\n}\n"


def test_compile_function_invalid_param_type(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn bad(x: Unknown): Void => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn bad(x: Unknown): Void => {}"


def test_compile_nested_braces_empty(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn nest(): Void => { { } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void nest() {\n    {\n    }\n}\n"


def test_compile_nested_braces_with_let(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn nest(): Void => { { let x: I32 = 1; } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void nest() {\n    {\n        int x = 1;\n    }\n}\n"


def test_compile_simple_if(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn check(): Void => { if (true) { let x: I32 = 1; } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void check() {\n    if (1) {\n        int x = 1;\n    }\n}\n"
    )


def test_compile_if_numeric_comparison(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn compare(): Void => { let x: I32 = 5; if (x < 10) { } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void compare() {\n    int x = 5;\n    if (x < 10) {\n    }\n}\n"


def test_compile_if_comparison_type_mismatch(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn bad(): Void => { let x: I32 = 1; let y: U8 = 2; if (x < y) { } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn bad(): Void => { let x: I32 = 1; let y: U8 = 2; if (x < y) { } }"


def test_compile_if_bool_equality(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn eq(): Void => { let flag: Bool = true; if (flag == false) { } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void eq() {\n    int flag = 1;\n    if (flag == 0) {\n    }\n}\n"


def test_compile_if_bool_numeric_mismatch(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn bad(): Void => { let flag: Bool = true; if (flag == 1) { } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn bad(): Void => { let flag: Bool = true; if (flag == 1) { } }"

def test_compile_nested_if_mutually_exclusive(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn check(x: I32): Void => { if (x > 10) { if (x < 10) { let y: I32 = 1; } } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "compiled: fn check(x: I32): Void => { if (x > 10) { if (x < 10) { let y: I32 = 1; } } }"
    )

def test_compile_function_call_no_args(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn callee(): Void => {}\nfn caller(): Void => { callee(); }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void callee() {\n}\nvoid caller() {\n    callee();\n}\n"


def test_compile_function_call_bool_arg(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn callee(value: Bool): Void => {}\nfn caller(): Void => { callee(true); }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void callee(int value) {\n}\nvoid caller() {\n    callee(1);\n}\n"


def test_compile_function_call_numeric_and_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn callee(x: I32, y: I32): Void => {}\nfn caller(): Void => { let num: I32 = 5; callee(num, 10); }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void callee(int x, int y) {\n}\nvoid caller() {\n    int num = 5;\n    callee(num, 10);\n}\n"


def test_compile_function_call_unknown_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn callee(x: I32): Void => {}\nfn caller(): Void => { callee(y); }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn callee(x: I32): Void => {}\nfn caller(): Void => { callee(y); }"


def test_compile_function_parameter_bounded_type(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn greater(x: I32 > 10): Void => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void greater(int x) {\n}\n"


def test_compile_function_call_bounded_literal_valid(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn greater(x: I32 > 10): Void => {}\nfn caller(): Void => { greater(20); }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == (
        "void greater(int x) {\n}\nvoid caller() {\n    greater(20);\n}\n"
    )


def test_compile_function_call_bounded_literal_invalid(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn greater(x: I32 > 10): Void => {}\nfn caller(): Void => { greater(0); }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "compiled: fn greater(x: I32 > 10): Void => {}\nfn caller(): Void => { greater(0); }"
    )


def test_compile_bounded_variable_declaration_from_if(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn check(): Void => { let x: I32 = 100; if (x > 10) { let y: I32 > 10 = x; } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void check() {\n    int x = 100;\n    if (x > 10) {\n        int y = x;\n    }\n}\n"
    )


def test_compile_bounded_variable_declaration_too_restrictive(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn check(): Void => { let x: I32 = 100; if (x > 10) { let y: I32 > 60 = x; } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "compiled: fn check(): Void => { let x: I32 = 100; if (x > 10) { let y: I32 > 60 = x; } }"
    )


def test_compile_variable_initialized_from_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn check(): Void => { let x: I32 = 100; if (x > 10) { let y: I32 = x; } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void check() {\n    int x = 100;\n    if (x > 10) {\n        int y = x;\n    }\n}\n"
    )


def test_compile_array_index_with_bounded_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn read(): Void => { let array: [U64; 2] = [100, 200]; let i: USize < array.length = 0; let val: U64 = array[i]; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void read() {\n    unsigned long long array[] = {100, 200};\n    unsigned long i = 0;\n    unsigned long long val = array[i];\n}\n"
    )


def test_compile_array_index_out_of_bounds(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn read(): Void => { let array: [U64; 2] = [100, 200]; let i = 100; let val = array[i]; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "compiled: fn read(): Void => { let array: [U64; 2] = [100, 200]; let i = 100; let val = array[i]; }"
    )


def test_compile_parentheses_in_let(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn paren(): Void => { let x: I32 = (1); }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void paren() {\n    int x = 1;\n}\n"


def test_compile_nested_parentheses_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn paren(): Void => { let x: I32 = 1; let y: I32 = (((x))); }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void paren() {\n    int x = 1;\n    int y = x;\n}\n"


def test_compile_parentheses_in_if_condition(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn check(): Void => { let x: I32 = 5; if ((x > 1)) { } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void check() {\n    int x = 5;\n    if (x > 1) {\n    }\n}\n"


def test_compile_parentheses_in_call_arg(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn callee(x: I32): Void => {}\nfn caller(): Void => { let a: I32 = 1; callee(((a))); }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void callee(int x) {\n}\nvoid caller() {\n    int a = 1;\n    callee(a);\n}\n"


def test_compile_arithmetic_literal(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn calc(): Void => { let x: I32 = 1 + 2 * 3 - 4; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void calc() {\n    int x = 1 + 2 * 3 - 4;\n}\n"


def test_compile_arithmetic_infer(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn calc(): Void => { let x = 1 + 2 * 3 - 4; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void calc() {\n    int x = 1 + 2 * 3 - 4;\n}\n"


def test_compile_arithmetic_parentheses_precedence(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn calc(): Void => { let x: I32 = (3 + 4) * 7; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void calc() {\n    int x = (3 + 4) * 7;\n}\n"


def test_compile_call_with_expression_arg(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn second(): I32 => { return 0; }\nfn first(x: I32): Void => {}\nfn caller(): Void => { first(200 + second()); }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "int second() {\n    return 0;\n}\nvoid first(int x) {\n}\nvoid caller() {\n    first(200 + second());\n}\n"
    )


def test_compile_nested_while_with_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn run(): I32 => { if (true) { let x = 100; while (true) { let y = 200; return 0; } } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "int run() {\n    if (1) {\n        int x = 100;\n        while (1) {\n            int y = 200;\n            return 0;\n        }\n    }\n}\n"
    )


def test_compile_break_in_while(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn loop(): Void => { while (true) { break; } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void loop() {\n    while (1) {\n        break;\n    }\n}\n"
    )


def test_compile_continue_in_while(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn loop(): Void => { while (true) { continue; } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void loop() {\n    while (1) {\n        continue;\n    }\n}\n"
    )


def test_compile_type_alias_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("type MyAlias = I16; fn foo(): Void => { let value: MyAlias = 100; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {\n    short value = 100;\n}\n"


def test_compile_global_struct_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Point {x : I32; y : I32;}\n\nlet myPoint: Point;"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    int x;\n    int y;\n};\nstruct Point myPoint;\n"
    )


def test_compile_struct_variable_inside_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Point {x : I32; y : I32;}\nfn foo(): Void => { let p: Point; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    int x;\n    int y;\n};\nvoid foo() {\n    struct Point p;\n}\n"
    )


def test_compile_struct_variable_unknown_type(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo(): Void => { let p: Unknown; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn foo(): Void => { let p: Unknown; }"


def test_compile_global_function_pointer(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("let myEmpty: () => Void;")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void (*myEmpty)();\n"


def test_compile_function_pointer_in_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo(): Void => { let cb: () => Void; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {\n    void (*cb)();\n}\n"


def test_compile_struct_init_global(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Point {x : I32; y : I32;}\n\nlet myPoint = Point {3, 4};"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    int x;\n    int y;\n};\nstruct Point myPoint;\nmyPoint.x = 3;\nmyPoint.y = 4;\n"
    )


def test_compile_struct_init_in_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Point {x : I32; y : I32;}\nfn foo(): Void => { let p = Point {3, 4}; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    int x;\n    int y;\n};\nvoid foo() {\n    struct Point p;\n    p.x = 3;\n    p.y = 4;\n}\n"
    )


def test_compile_generic_struct_monomorph(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Wrapper<T> {value : T}\n\nlet value: Wrapper<I32> = Wrapper {100};"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Wrapper_I32 {\n    int value;\n};\nstruct Wrapper_I32 value;\nvalue.value = 100;\n"
    )


def test_compile_struct_literal_field_access(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Wrapper { value : I32 }\nfn foo(): Void => { let inner = (Wrapper {100}).value; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Wrapper {\n    int value;\n};\nvoid foo() {\n    int inner = 100;\n}\n"
    )


def test_compile_class_fn_shorthand(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("class fn Point(x: U32, y: U32) => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    unsigned int x;\n    unsigned int y;\n};\nstruct Point Point(unsigned int x, unsigned int y) {\n    struct Point this;\n    this.x = x;\n    this.y = y;\n    return this;\n}\n"
    )


def test_compile_class_fn_with_method(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "class fn Point(x: U32, y: U32) => { fn empty() => { } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    unsigned int x;\n    unsigned int y;\n};\nvoid empty_Point(struct Point this) {\n}\nstruct Point Point(unsigned int x, unsigned int y) {\n    struct Point this;\n    this.x = x;\n    this.y = y;\n    return this;\n}\n"
    )


def test_compile_class_fn_method_return_this(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "class fn Point(x: U32, y: U32) => { fn empty() => { return this; } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    unsigned int x;\n    unsigned int y;\n};\nstruct Point empty_Point(struct Point this) {\n    return this;\n}\nstruct Point Point(unsigned int x, unsigned int y) {\n    struct Point this;\n    this.x = x;\n    this.y = y;\n    return this;\n}\n"
    )


def test_compile_flatten_inner_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn outer() => { fn inner() => {} }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct outer_t {\n};\nvoid inner_outer(struct outer_t this) {\n}\nvoid outer() {\n}\n"
    )


def test_compile_inner_function_with_declaration(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn parent() => { let myValue : I32 = 100; fn child(something : I32) => { } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct parent_t {\n    int myValue;\n};\nvoid child_parent(struct parent_t this, int something) {\n}\nvoid parent() {\n    struct parent_t this;\n    this.myValue = 100;\n}\n"
    )


def test_compile_inner_function_param_capture(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn outer(myParam : I32) => { fn inner() => { } }")
    
def test_compile_inner_function_with_inferred_declaration(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn parent() => { let first = 100; fn child(something : I32) => { } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct parent_t {\n    int first;\n};\nvoid child_parent(struct parent_t this, int something) {\n}\nvoid parent() {\n    struct parent_t this;\n    this.first = 100;\n}\n"
    )


def test_compile_implicit_int_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn first() => { return 100; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int first() {\n    return 100;\n}\n"


def test_compile_class_fn_return_consistency(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "class fn Item() => {}\n\nclass fn Factory() => {\n fn create() => {\n  return Item();\n }\n}"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Item {\n};\nstruct Factory {\n};\nstruct Item Item() {\n    struct Item this;\n    return this;\n}\nstruct Item create_Factory(struct Factory this) {\n    return Item();\n}\nstruct Factory Factory() {\n    struct Factory this;\n    return this;\n}\n"
    )
