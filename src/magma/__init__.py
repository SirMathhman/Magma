from pathlib import Path
import re


class Compiler:
    """Minimal compiler skeleton.

    The compiler operates on files to keep the interface simple for now.  This
    mirrors how traditional compilers work and makes future CLI integration
    straightforward.
    """

    NUMERIC_TYPE_MAP = {
        "u8": "unsigned char",
        "u16": "unsigned short",
        "u32": "unsigned int",
        "u64": "unsigned long long",
        "i8": "signed char",
        "i16": "short",
        "i32": "int",
        "i64": "long long",
    }

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

        funcs = []
        pattern = re.compile(
            r"fn\s+(\w+)\s*\(\s*\)\s*(?::\s*(Void|Bool|U8|U16|U32|U64|I8|I16|I32|I64)\s*)?=>\s*{\s*(.*?)\s*}\s*",
            re.IGNORECASE | re.DOTALL,
        )

        pos = 0
        for match in pattern.finditer(source):
            if source[pos:match.start()].strip():
                Path(output_path).write_text(f"compiled: {source}")
                return

            name = match.group(1)
            ret_type = match.group(2)
            body = match.group(3).strip()

            if ret_type is None or ret_type.lower() == "void":
                if body:
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                funcs.append(f"void {name}() {{}}\n")
            elif ret_type.lower() == "bool":
                lower_body = body.lower()
                if lower_body not in {"return true;", "return false;"}:
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                # emit valid C without relying on additional headers
                return_value = "1" if lower_body == "return true;" else "0"
                funcs.append(f"int {name}() {{ return {return_value}; }}\n")
            elif ret_type.lower() in self.NUMERIC_TYPE_MAP:
                if body != "return 0;":
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                c_type = self.NUMERIC_TYPE_MAP[ret_type.lower()]
                funcs.append(f"{c_type} {name}() {{ return 0; }}\n")
            else:
                Path(output_path).write_text(f"compiled: {source}")
                return

            pos = match.end()

        if source[pos:].strip():
            Path(output_path).write_text(f"compiled: {source}")
            return

        if funcs:
            Path(output_path).write_text("".join(funcs))
        else:
            Path(output_path).write_text(f"compiled: {source}")

