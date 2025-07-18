import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'src'))

from magma import Compiler


def test_compile_returns_placeholder():
    compiler = Compiler()
    result = compiler.compile("hello")
    assert result == "compiled: hello"

