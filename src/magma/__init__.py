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
            Path(output_path).write_text("int main() {\n}\n")
            return

        funcs = []
        structs = []
        header_pattern = re.compile(
            r"fn\s+(\w+)\s*\(\s*(.*?)\s*\)\s*(?::\s*(Void|Bool|U8|U16|U32|U64|I8|I16|I32|I64)\s*)?=>\s*{",
            re.IGNORECASE | re.DOTALL,
        )
        param_pattern = re.compile(
            r"(\w+)\s*:\s*(Bool|U8|U16|U32|U64|I8|I16|I32|I64)",
            re.IGNORECASE,
        )
        struct_pattern = re.compile(
            r"struct\s+(\w+)\s*{\s*(.*?)\s*}\s*",
            re.IGNORECASE | re.DOTALL,
        )
        field_pattern = re.compile(
            r"(\w+)\s*:\s*(Bool|U8|U16|U32|U64|I8|I16|I32|I64)",
            re.IGNORECASE,
        )
        let_pattern = re.compile(
            r"let\s+(mut\s+)?(\w+)(?:\s*:\s*(.*?))?\s*=\s*(.+?)\s*;",
            re.IGNORECASE | re.DOTALL,
        )
        assign_pattern = re.compile(r"(\w+)\s*=\s*(.+?)\s*;", re.IGNORECASE | re.DOTALL)
        call_pattern = re.compile(r"(\w+)\s*\((.*?)\)\s*;", re.DOTALL)
        if_pattern = re.compile(r"if\s*\((.+?)\)\s*{", re.DOTALL)
        array_type_pattern = re.compile(
            r"\[\s*(Bool|U8|U16|U32|U64|I8|I16|I32|I64)\s*;\s*([0-9]+)\s*\]",
            re.IGNORECASE,
        )
        array_value_pattern = re.compile(r"\[\s*(.*?)\s*\]", re.DOTALL)

        def extract_braced_block(text: str, start: int):
            depth = 0
            if start >= len(text) or text[start] != "{":
                return None, start
            pos = start
            while pos < len(text):
                if text[pos] == "{":
                    depth += 1
                elif text[pos] == "}":
                    depth -= 1
                    if depth == 0:
                        return text[start + 1 : pos], pos + 1
                pos += 1
            return None, start

        def compile_block(block: str, indent: int, variables: dict):
            pos2 = 0
            lines = []
            indent_str = " " * (indent * 4)
            while pos2 < len(block):
                ws_body = re.match(r"\s*", block[pos2:])
                pos2 += ws_body.end()
                if pos2 >= len(block):
                    break

                if block[pos2] == "{":
                    inner, new_pos = extract_braced_block(block, pos2)
                    if inner is None:
                        return None
                    sub_lines = compile_block(inner, indent + 1, variables)
                    if sub_lines is None:
                        return None
                    lines.append(indent_str + "{")
                    lines.extend(sub_lines)
                    lines.append(indent_str + "}")
                    pos2 = new_pos
                    continue

                if_match = if_pattern.match(block, pos2)
                if if_match:
                    condition = if_match.group(1).strip()
                    inner, new_pos = extract_braced_block(block, if_match.end() - 1)
                    if inner is None:
                        return None

                    comp_match = re.match(r"(.+?)\s*(==|<=|>=|<|>)\s*(.+)", condition)
                    if condition.lower() in {"true", "false"}:
                        cond_c = "1" if condition.lower() == "true" else "0"
                    elif comp_match:
                        left = comp_match.group(1).strip()
                        op = comp_match.group(2)
                        right = comp_match.group(3).strip()

                        def expr_type(expr: str):
                            if expr.lower() in {"true", "false"}:
                                return "bool"
                            if re.fullmatch(r"[0-9]+", expr):
                                return "i32"
                            if expr in variables:
                                return variables[expr]["type"]
                            return None

                        l_type = expr_type(left)
                        r_type = expr_type(right)
                        if l_type is None or r_type is None or l_type != r_type:
                            return None
                        def to_c(expr: str, typ: str):
                            if typ == "bool":
                                if expr.lower() == "true":
                                    return "1"
                                if expr.lower() == "false":
                                    return "0"
                            return expr

                        cond_c = f"{to_c(left, l_type)} {op} {to_c(right, r_type)}"
                    else:
                        cond_c = condition

                    sub_lines = compile_block(inner, indent + 1, variables)
                    if sub_lines is None:
                        return None
                    lines.append(f"{indent_str}if ({cond_c}) {{")
                    lines.extend(sub_lines)
                    lines.append(f"{indent_str}}}")
                    pos2 = new_pos
                    continue

                let_match = let_pattern.match(block, pos2)
                if let_match:
                    mutable = let_match.group(1) is not None
                    var_name = let_match.group(2)
                    var_type = let_match.group(3)
                    value = let_match.group(4).strip()

                    var_type = var_type.strip() if var_type else None

                    if var_type and var_type.lower() == "void":
                        return None

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
                            return None
                        lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                    else:
                        array_type = array_type_pattern.fullmatch(var_type)
                        if array_type:
                            elem_type = array_type.group(1)
                            size = int(array_type.group(2))
                            value_match = array_value_pattern.fullmatch(value)
                            if not value_match:
                                return None
                            elems = [v.strip() for v in value_match.group(1).split(',') if v.strip()]
                            if len(elems) != size:
                                return None
                            c_elems = []
                            if elem_type.lower() == "bool":
                                for val in elems:
                                    if val.lower() not in {"true", "false"}:
                                        return None
                                    c_elems.append("1" if val.lower() == "true" else "0")
                                c_type = "int"
                            else:
                                if elem_type.lower() not in self.NUMERIC_TYPE_MAP:
                                    return None
                                for val in elems:
                                    if not re.fullmatch(r"[0-9]+", val):
                                        return None
                                    c_elems.append(val)
                                c_type = self.NUMERIC_TYPE_MAP[elem_type.lower()]
                            lines.append(f"{indent_str}{c_type} {var_name}[] = {{{', '.join(c_elems)}}};")
                            magma_type = f"[{elem_type};{size}]"
                        elif var_type.lower() == "bool":
                            if value.lower() not in {"true", "false"}:
                                return None
                            c_value = "1" if value.lower() == "true" else "0"
                            c_type = "int"
                            lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                            magma_type = "bool"
                        elif var_type.lower() in self.NUMERIC_TYPE_MAP:
                            if not re.fullmatch(r"[0-9]+", value):
                                return None
                            c_type = self.NUMERIC_TYPE_MAP[var_type.lower()]
                            lines.append(f"{indent_str}{c_type} {var_name} = {value};")
                            magma_type = var_type.lower()
                        else:
                            return None

                    variables[var_name] = {
                        "type": magma_type,
                        "c_type": c_type,
                        "mutable": mutable,
                    }

                    pos2 = let_match.end()
                    continue

                assign_match = assign_pattern.match(block, pos2)
                if assign_match:
                    var_name = assign_match.group(1)
                    value = assign_match.group(2).strip()

                    if var_name not in variables or not variables[var_name]["mutable"]:
                        return None

                    var_type = variables[var_name]["type"]
                    if var_type == "bool":
                        if value.lower() not in {"true", "false"}:
                            return None
                        c_value = "1" if value.lower() == "true" else "0"
                    elif var_type in self.NUMERIC_TYPE_MAP or var_type == "i32":
                        if not re.fullmatch(r"[0-9]+", value):
                            return None
                        c_value = value
                    else:
                        return None

                    lines.append(f"{indent_str}{var_name} = {c_value};")
                    pos2 = assign_match.end()
                    continue

                call_match = call_pattern.match(block, pos2)
                if call_match:
                    name = call_match.group(1)
                    args_src = call_match.group(2).strip()
                    c_args = []
                    if args_src:
                        arg_items = [a.strip() for a in args_src.split(',') if a.strip()]
                        for arg in arg_items:
                            if arg.lower() in {"true", "false"}:
                                c_args.append("1" if arg.lower() == "true" else "0")
                            elif re.fullmatch(r"[0-9]+", arg):
                                c_args.append(arg)
                            elif arg in variables:
                                c_args.append(arg)
                            else:
                                return None
                    lines.append(f"{indent_str}{name}({', '.join(c_args)});")
                    pos2 = call_match.end()
                    continue

                return None

            return lines

        pos = 0
        while pos < len(source):
            ws = re.match(r"\s*", source[pos:])
            pos += ws.end()
            if pos >= len(source):
                break

            struct_match = struct_pattern.match(source, pos)
            if struct_match:
                name = struct_match.group(1)
                fields_src = struct_match.group(2).strip()
                c_fields = []
                if fields_src:
                    fields = [f.strip() for f in fields_src.split(';') if f.strip()]
                    for field in fields:
                        field_match = field_pattern.fullmatch(field)
                        if not field_match:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        fname, ftype = field_match.groups()
                        if ftype.lower() == "bool":
                            c_type = "int"
                        elif ftype.lower() in self.NUMERIC_TYPE_MAP:
                            c_type = self.NUMERIC_TYPE_MAP[ftype.lower()]
                        else:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        c_fields.append(f"{c_type} {fname};")
                if c_fields:
                    joined = "\n    ".join(c_fields)
                    structs.append(f"struct {name} {{\n    {joined}\n}};\n")
                else:
                    structs.append(f"struct {name} {{\n}};\n")
                pos = struct_match.end()
                continue

            header_match = header_pattern.match(source, pos)
            if not header_match:
                Path(output_path).write_text(f"compiled: {source}")
                return

            name = header_match.group(1)
            params_src = header_match.group(2).strip()
            ret_type = header_match.group(3)
            body_str, pos = extract_braced_block(source, header_match.end() - 1)
            if body_str is None:
                Path(output_path).write_text(f"compiled: {source}")
                return

            c_params = []
            if params_src:
                params = [p.strip() for p in params_src.split(',') if p.strip()]
                for param in params:
                    param_match = param_pattern.fullmatch(param)
                    if not param_match:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    p_name, p_type = param_match.groups()
                    if p_type.lower() == "bool":
                        p_c_type = "int"
                    elif p_type.lower() in self.NUMERIC_TYPE_MAP:
                        p_c_type = self.NUMERIC_TYPE_MAP[p_type.lower()]
                    else:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    c_params.append(f"{p_c_type} {p_name}")
            param_list = ", ".join(c_params)

            if ret_type is None or ret_type.lower() == "void":
                if body_str:
                    variables = {}
                    lines = compile_block(body_str, 1, variables)
                    if lines is None:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    body_text = "\n".join(lines)
                    if body_text:
                        body_text = body_text + "\n"
                    funcs.append(f"void {name}({param_list}) {{\n{body_text}}}\n")
                else:
                    funcs.append(f"void {name}({param_list}) {{\n}}\n")
            elif ret_type.lower() == "bool":
                lower_body = body_str.lower().strip()
                if lower_body not in {"return true;", "return false;"}:
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                return_value = "1" if lower_body == "return true;" else "0"
                funcs.append(
                    f"int {name}({param_list}) {{\n    return {return_value};\n}}\n"
                )
            elif ret_type.lower() in self.NUMERIC_TYPE_MAP:
                if body_str.strip() != "return 0;":
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                c_type = self.NUMERIC_TYPE_MAP[ret_type.lower()]
                funcs.append(
                    f"{c_type} {name}({param_list}) {{\n    return 0;\n}}\n"
                )
            else:
                Path(output_path).write_text(f"compiled: {source}")
                return

        output = "".join(structs) + "".join(funcs)
        if output:
            Path(output_path).write_text(output)
        else:
            Path(output_path).write_text(f"compiled: {source}")

