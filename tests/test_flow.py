import sys
from pathlib import Path
import pytest

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "src"))

from magma import Compiler

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


def test_compile_function_with_array_param(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn fill(buf: [I32; 4]): Void => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void fill(int buf[4]) {\n}\n"


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


