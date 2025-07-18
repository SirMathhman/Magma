from pathlib import Path
import re


class Compiler:
    """Minimal compiler skeleton.

    The compiler operates on files to keep the interface simple for now.  This
    mirrors how traditional compilers work and makes future CLI integration
    straightforward.
    """

    def compile(self, input_path: Path, output_path: Path) -> None:
        """Compile ``input_path`` to ``output_path``.

        An empty input file results in a minimal C program with an empty
        ``main`` function.  Non-empty input is currently passed through as a
        placeholder to keep the initial skeleton trivial.
        """

        source = Path(input_path).read_text()

        if not source.strip():
            Path(output_path).write_text("int main() {}\n")
            return

        match = re.fullmatch(r"fn\s+(\w+)\s*\(\)\s*=>\s*{}\s*", source.strip())
        if match:
            name = match.group(1)
            Path(output_path).write_text(f"void {name}() {{}}\n")
        else:
            Path(output_path).write_text(f"compiled: {source}")

