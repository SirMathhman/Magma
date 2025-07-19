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
        let_pattern = re.compile(
            r"let\s+(mut\s+)?(\w+)(?:\s*:\s*(.*?))?\s*=\s*(.+?)\s*;",
            re.IGNORECASE | re.DOTALL,
        )
        assign_pattern = re.compile(r"(\w+)\s*=\s*(.+?)\s*;", re.IGNORECASE | re.DOTALL)
        array_type_pattern = re.compile(
            r"\[\s*(Bool|U8|U16|U32|U64|I8|I16|I32|I64)\s*;\s*([0-9]+)\s*\]",
            re.IGNORECASE,
        )
        array_value_pattern = re.compile(r"\[\s*(.*?)\s*\]", re.DOTALL)

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
                    pos2 = 0
                    decls = []
                    variables = {}
                    while pos2 < len(body):
                        # skip whitespace
                        ws = re.match(r"\s*", body[pos2:])
                        pos2 += ws.end()
                        if pos2 >= len(body):
                            break

                        let_match = let_pattern.match(body, pos2)
                        if let_match:
                            mutable = let_match.group(1) is not None
                            var_name = let_match.group(2)
                            var_type = let_match.group(3)
                            value = let_match.group(4).strip()

                            var_type = var_type.strip() if var_type else None

                            if var_type and var_type.lower() == "void":
                                Path(output_path).write_text(f"compiled: {source}")
                                return

                            if var_type is None:
                                if value.lower() in {"true", "false"}:
                                    c_value = "1" if value.lower() == "true" else "0"
                                    c_type = "int"
                                    magma_type = "bool"
                                elif re.fullmatch(r"[0-9]+", value):
                                    c_value = value
                                    c_type = "int"
                                    magma_type = "i32"
                                else:
                                    Path(output_path).write_text(f"compiled: {source}")
                                    return
                                decls.append(f"{c_type} {var_name} = {c_value};")
                            else:
                                array_type = array_type_pattern.fullmatch(var_type)
                                if array_type:
                                    elem_type = array_type.group(1)
                                    size = int(array_type.group(2))
                                    value_match = array_value_pattern.fullmatch(value)
                                    if not value_match:
                                        Path(output_path).write_text(f"compiled: {source}")
                                        return
                                    elems = [v.strip() for v in value_match.group(1).split(',') if v.strip()]
                                    if len(elems) != size:
                                        Path(output_path).write_text(f"compiled: {source}")
                                        return
                                    c_elems = []
                                    if elem_type.lower() == "bool":
                                        for val in elems:
                                            if val.lower() not in {"true", "false"}:
                                                Path(output_path).write_text(f"compiled: {source}")
                                                return
                                            c_elems.append("1" if val.lower() == "true" else "0")
                                        c_type = "int"
                                    else:
                                        if elem_type.lower() not in self.NUMERIC_TYPE_MAP:
                                            Path(output_path).write_text(f"compiled: {source}")
                                            return
                                        for val in elems:
                                            if not re.fullmatch(r"[0-9]+", val):
                                                Path(output_path).write_text(f"compiled: {source}")
                                                return
                                            c_elems.append(val)
                                        c_type = self.NUMERIC_TYPE_MAP[elem_type.lower()]
                                    decls.append(f"{c_type} {var_name}[] = {{{', '.join(c_elems)}}};")
                                    magma_type = f"[{elem_type};{size}]"
                                elif var_type.lower() == "bool":
                                    if value.lower() not in {"true", "false"}:
                                        Path(output_path).write_text(f"compiled: {source}")
                                        return
                                    c_value = "1" if value.lower() == "true" else "0"
                                    c_type = "int"
                                    decls.append(f"{c_type} {var_name} = {c_value};")
                                    magma_type = "bool"
                                elif var_type.lower() in self.NUMERIC_TYPE_MAP:
                                    if not re.fullmatch(r"[0-9]+", value):
                                        Path(output_path).write_text(f"compiled: {source}")
                                        return
                                    c_type = self.NUMERIC_TYPE_MAP[var_type.lower()]
                                    decls.append(f"{c_type} {var_name} = {value};")
                                    magma_type = var_type.lower()
                                else:
                                    Path(output_path).write_text(f"compiled: {source}")
                                    return

                            variables[var_name] = {
                                "type": magma_type,
                                "c_type": c_type,
                                "mutable": mutable,
                            }

                            pos2 = let_match.end()
                            continue

                        assign_match = assign_pattern.match(body, pos2)
                        if assign_match:
                            var_name = assign_match.group(1)
                            value = assign_match.group(2).strip()

                            if var_name not in variables or not variables[var_name]["mutable"]:
                                Path(output_path).write_text(f"compiled: {source}")
                                return

                            var_type = variables[var_name]["type"]
                            if var_type == "bool":
                                if value.lower() not in {"true", "false"}:
                                    Path(output_path).write_text(f"compiled: {source}")
                                    return
                                c_value = "1" if value.lower() == "true" else "0"
                            elif var_type in self.NUMERIC_TYPE_MAP or var_type == "i32":
                                if not re.fullmatch(r"[0-9]+", value):
                                    Path(output_path).write_text(f"compiled: {source}")
                                    return
                                c_value = value
                            else:
                                Path(output_path).write_text(f"compiled: {source}")
                                return

                            decls.append(f"{var_name} = {c_value};")
                            pos2 = assign_match.end()
                            continue

                        if body[pos2:].strip():
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        break

                    if pos2 < len(body) and body[pos2:].strip():
                        Path(output_path).write_text(f"compiled: {source}")
                        return

                    funcs.append(f"void {name}() {{ {' '.join(decls)} }}\n")
                else:
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

