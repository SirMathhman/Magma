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

    assert output_file.read_text() == "int main() {}\n"


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

    assert output_file.read_text() == "void foo() {}\n"



def test_compile_multiple_functions(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo() => {}\nfn bar() => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {}\nvoid bar() {}\n"


def test_compile_explicit_void_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn empty(): Void => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void empty() {}\n"


def test_compile_bool_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn truth(): Bool => { return true; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int truth() { return 1; }\n"


def test_compile_bool_false_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn lie(): Bool => { return false; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int lie() { return 0; }\n"


def test_compile_function_with_extra_whitespace(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn   spaced  (  )  :  Void  =>  {   }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void spaced() {}\n"


def test_compile_bool_with_whitespace(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn ws(): Bool => {  return true;  }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int ws() { return 1; }\n"


def test_compile_function_with_newlines(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn nl\n(\n)\n:\nVoid\n=>\n{\n}\n")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void nl() {}\n"


def test_compile_function_with_carriage_returns(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    content = "fn cr\r\n(\r\n)\r\n:\r\nVoid\r\n=>\r\n{\r\n}\r\n"
    input_file.write_text(content)
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void cr() {}\n"


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

    assert output_file.read_text() == f"{c_type} num() {{ return 0; }}\n"


def test_compile_let_numeric(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo(): Void => { let myValue: I32 = 420; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() { int myValue = 420; }\n"


def test_compile_let_bool(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn flag(): Void => { let flag: Bool = true; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void flag() { int flag = 1; }\n"


def test_compile_let_invalid_value(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn bad(): Void => { let nope: I32 = true; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn bad(): Void => { let nope: I32 = true; }"
