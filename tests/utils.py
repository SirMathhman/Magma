import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'src'))

from magma import Compiler
import signal


class CompileTimeout(Exception):
    pass


def _timeout_handler(signum, frame):
    raise CompileTimeout


def compile_source(tmp_path, source: str) -> str:
    """Compile ``source`` with a 3s timeout and return generated code."""

    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    output_file = tmp_path / "out.c"
    input_file.write_text(source)

    has_alarm = hasattr(signal, "SIGALRM")
    if has_alarm:
        old_handler = signal.signal(signal.SIGALRM, _timeout_handler)
        signal.alarm(3)
    try:
        compiler.compile(input_file, output_file)
    finally:
        if has_alarm:
            signal.alarm(0)
            signal.signal(signal.SIGALRM, old_handler)

    return output_file.read_text()
