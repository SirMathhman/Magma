import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'src'))

from magma import Compiler


def compile_source(tmp_path, source: str) -> str:
    """Compile ``source`` using a temporary directory and return generated code."""
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    output_file = tmp_path / "out.c"
    input_file.write_text(source)
    compiler.compile(input_file, output_file)
    return output_file.read_text()
