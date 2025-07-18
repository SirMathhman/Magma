import sys
from pathlib import Path

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
