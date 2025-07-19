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
        "usize": "unsigned long",
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
            r"fn\s+(\w+)\s*\(\s*(.*?)\s*\)\s*(?::\s*(Void|Bool|U8|U16|U32|U64|USize|I8|I16|I32|I64)\s*)?=>\s*{",
            re.IGNORECASE | re.DOTALL,
        )
        param_pattern = re.compile(
            r"(\w+)\s*:\s*(Bool|U8|U16|U32|U64|USize|I8|I16|I32|I64)(?:\s*(<=|>=|<|>|==)\s*([0-9]+))?",
            re.IGNORECASE,
        )
        struct_pattern = re.compile(
            r"struct\s+(\w+)\s*{\s*(.*?)\s*}\s*",
            re.IGNORECASE | re.DOTALL,
        )
        field_pattern = re.compile(
            r"(\w+)\s*:\s*(Bool|U8|U16|U32|U64|USize|I8|I16|I32|I64)",
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
            r"\[\s*(Bool|U8|U16|U32|U64|USize|I8|I16|I32|I64)\s*;\s*([0-9]+)\s*\]",
            re.IGNORECASE,
        )
        array_value_pattern = re.compile(r"\[\s*(.*?)\s*\]", re.DOTALL)
        bounded_type_pattern = re.compile(
            r"(Bool|U8|U16|U32|U64|USize|I8|I16|I32|I64)(?:\s*(<=|>=|<|>|==)\s*([0-9]+|\w+\.length))?",
            re.IGNORECASE,
        )
        index_pattern = re.compile(r"(\w+)\s*\[\s*(.+?)\s*\]")

        func_sigs = {}

        def strip_parens(expr: str) -> str:
            expr = expr.strip()
            while (
                expr.startswith("(")
                and expr.endswith(")")
            ):
                depth = 0
                balanced = True
                for i, ch in enumerate(expr):
                    if ch == "(":
                        depth += 1
                    elif ch == ")":
                        depth -= 1
                        if depth == 0 and i != len(expr) - 1:
                            balanced = False
                            break
                    if depth < 0:
                        balanced = False
                        break
                if balanced and depth == 0:
                    expr = expr[1:-1].strip()
                    continue
                break
            return expr

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

        def parse_arithmetic(expr: str):
            token = expr.replace(" ", "")
            if not token or not re.fullmatch(r"[0-9+\-*/()]+", token):
                return None
            try:
                return eval(token, {"__builtins__": {}})
            except Exception:
                return None

        def compile_block(block: str, indent: int, variables: dict, func_sigs: dict):
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
                    sub_lines = compile_block(inner, indent + 1, variables, func_sigs)
                    if sub_lines is None:
                        return None
                    lines.append(indent_str + "{")
                    lines.extend(sub_lines)
                    lines.append(indent_str + "}")
                    pos2 = new_pos
                    continue

                if_match = if_pattern.match(block, pos2)
                if if_match:
                    condition = strip_parens(if_match.group(1))
                    inner, new_pos = extract_braced_block(block, if_match.end() - 1)
                    if inner is None:
                        return None

                    comp_match = re.match(r"(.+?)\s*(==|<=|>=|<|>)\s*(.+)", condition)
                    if condition.lower() in {"true", "false"}:
                        cond_c = "1" if condition.lower() == "true" else "0"
                    elif comp_match:
                        left = strip_parens(comp_match.group(1))
                        op = comp_match.group(2)
                        right = strip_parens(comp_match.group(3))

                        def expr_type(expr: str):
                            expr = strip_parens(expr)
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

                    sub_lines = compile_block(inner, indent + 1, variables, func_sigs)
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
                    value = strip_parens(let_match.group(4))

                    var_type = var_type.strip() if var_type else None
                    magma_bound = None
                    array_type = None

                    if var_type and var_type.lower() == "void":
                        return None

                    if var_type is None:
                        index_match = index_pattern.fullmatch(value)
                        if value.lower() in {"true", "false"}:
                            c_value = "1" if value.lower() == "true" else "0"
                            c_type = "int"
                            magma_type = "bool"
                        elif re.fullmatch(r"[0-9]+", value) or parse_arithmetic(value) is not None:
                            c_value = value
                            c_type = "int"
                            magma_type = "i32"
                        elif index_match:
                            arr_name, idx_token = index_match.groups()
                            idx_token = strip_parens(idx_token)
                            idx_token = strip_parens(idx_token)
                            if arr_name not in variables or "length" not in variables[arr_name]:
                                return None
                            arr_info = variables[arr_name]
                            arr_len = arr_info["length"]
                            if re.fullmatch(r"[0-9]+", idx_token):
                                if int(idx_token) >= arr_len:
                                    return None
                                idx_c = idx_token
                            elif idx_token in variables:
                                idx_info = variables[idx_token]
                                if idx_info.get("bound") != ("<", arr_len):
                                    return None
                                idx_c = idx_token
                            else:
                                return None
                            c_type = arr_info["c_type"]
                            magma_type = arr_info["elem_type"]
                            c_value = f"{arr_name}[{idx_c}]"
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
                            variables[var_name] = {
                                "type": magma_type,
                                "c_type": c_type,
                                "mutable": mutable,
                                "bound": magma_bound,
                                "length": size,
                                "elem_type": elem_type.lower(),
                            }
                            pos2 = let_match.end()
                            continue
                        elif var_type.lower() == "bool":
                            if value.lower() in {"true", "false"}:
                                c_value = "1" if value.lower() == "true" else "0"
                            elif value in variables and variables[value]["type"] == "bool":
                                c_value = value
                            else:
                                return None
                            c_type = "int"
                            lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                            magma_type = "bool"
                        elif bounded_type_match := bounded_type_pattern.fullmatch(var_type):
                            base_type = bounded_type_match.group(1)
                            bound_op = bounded_type_match.group(2)
                            bound_val = bounded_type_match.group(3)
                            if base_type.lower() not in self.NUMERIC_TYPE_MAP:
                                return None
                            c_type = self.NUMERIC_TYPE_MAP[base_type.lower()]
                            magma_type = base_type.lower()
                            bound = None
                            if bound_op:
                                if bound_val.endswith(".length"):
                                    arr_name = bound_val[: -len(".length")]
                                    if arr_name not in variables or "length" not in variables[arr_name]:
                                        return None
                                    bound = (bound_op, variables[arr_name]["length"])
                                else:
                                    bound = (bound_op, int(bound_val))
                            index_match = index_pattern.fullmatch(value)
                            if index_match:
                                arr_name, idx_token = index_match.groups()
                                idx_token = strip_parens(idx_token)
                                if arr_name not in variables or "length" not in variables[arr_name]:
                                    return None
                                arr_info = variables[arr_name]
                                if arr_info["elem_type"] != magma_type:
                                    return None
                                arr_len = arr_info["length"]
                                if re.fullmatch(r"[0-9]+", idx_token):
                                    if int(idx_token) >= arr_len:
                                        return None
                                    idx_c = idx_token
                                elif idx_token in variables and variables[idx_token].get("bound") == ("<", arr_len):
                                    idx_c = idx_token
                                else:
                                    return None
                                c_value = f"{arr_name}[{idx_c}]"
                            elif re.fullmatch(r"[0-9]+", value) or parse_arithmetic(value) is not None:
                                if bound:
                                    eval_val = parse_arithmetic(value)
                                    if eval_val is None:
                                        eval_val = int(value)
                                    arg_val = eval_val
                                    op, val_b = bound
                                    if op == ">" and not (arg_val > val_b):
                                        return None
                                    if op == "<" and not (arg_val < val_b):
                                        return None
                                    if op == ">=" and not (arg_val >= val_b):
                                        return None
                                    if op == "<=" and not (arg_val <= val_b):
                                        return None
                                    if op == "==" and not (arg_val == val_b):
                                        return None
                                c_value = value
                            elif value in variables and variables[value]["type"] in self.NUMERIC_TYPE_MAP:
                                if bound:
                                    other_bound = variables[value].get("bound")
                                    if other_bound != bound:
                                        return None
                                c_value = value
                            else:
                                return None
                            lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                            magma_bound = bound
                            magma_type = magma_type
                        elif index_match := index_pattern.fullmatch(value):
                            arr_name, idx_token = index_match.groups()
                            idx_token = strip_parens(idx_token)
                            if arr_name not in variables or "length" not in variables[arr_name]:
                                return None
                            arr_info = variables[arr_name]
                            if arr_info["elem_type"] != var_type.lower():
                                return None
                            arr_len = arr_info["length"]
                            if re.fullmatch(r"[0-9]+", idx_token):
                                if int(idx_token) >= arr_len:
                                    return None
                                idx_c = idx_token
                            elif idx_token in variables and variables[idx_token].get("bound") == ("<", arr_len):
                                idx_c = idx_token
                            else:
                                return None
                            c_value = f"{arr_name}[{idx_c}]"
                            c_type = arr_info["c_type"]
                            magma_type = var_type.lower()
                        elif var_type.lower() in self.NUMERIC_TYPE_MAP:
                            c_type = self.NUMERIC_TYPE_MAP[var_type.lower()]
                            if re.fullmatch(r"[0-9]+", value) or parse_arithmetic(value) is not None:
                                c_value = value
                            elif value in variables and variables[value]["type"] in self.NUMERIC_TYPE_MAP:
                                c_value = value
                            else:
                                return None
                            lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                            magma_type = var_type.lower()
                        else:
                            return None

                    if var_name not in variables:
                        variables[var_name] = {}
                    variables[var_name].update({
                        "type": magma_type,
                        "c_type": c_type,
                        "mutable": mutable,
                        "bound": magma_bound,
                    })
                    if array_type:
                        variables[var_name]["length"] = size
                        variables[var_name]["elem_type"] = elem_type.lower()

                    pos2 = let_match.end()
                    continue

                assign_match = assign_pattern.match(block, pos2)
                if assign_match:
                    var_name = assign_match.group(1)
                    value = strip_parens(assign_match.group(2))

                    if var_name not in variables or not variables[var_name]["mutable"]:
                        return None

                    var_type = variables[var_name]["type"]
                    index_match = index_pattern.fullmatch(value)
                    if var_type == "bool":
                        if value.lower() not in {"true", "false"}:
                            return None
                        c_value = "1" if value.lower() == "true" else "0"
                    elif var_type in self.NUMERIC_TYPE_MAP or var_type == "i32":
                        if re.fullmatch(r"[0-9]+", value) or parse_arithmetic(value) is not None:
                            c_value = value
                        elif index_match:
                            arr_name, idx_token = index_match.groups()
                            idx_token = strip_parens(idx_token)
                            if arr_name not in variables or "length" not in variables[arr_name]:
                                return None
                            arr_info = variables[arr_name]
                            if arr_info["elem_type"] != var_type:
                                return None
                            arr_len = arr_info["length"]
                            if re.fullmatch(r"[0-9]+", idx_token):
                                if int(idx_token) >= arr_len:
                                    return None
                                idx_c = idx_token
                            elif idx_token in variables and variables[idx_token].get("bound") == ("<", arr_len):
                                idx_c = idx_token
                            else:
                                return None
                            c_value = f"{arr_name}[{idx_c}]"
                        else:
                            if value in variables and variables[value]["type"] in self.NUMERIC_TYPE_MAP:
                                c_value = value
                            else:
                                return None
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
                    arg_items = []
                    if args_src:
                        arg_items = [a.strip() for a in args_src.split(',') if a.strip()]
                    sig = func_sigs.get(name)
                    if sig and len(arg_items) != len(sig["params"]):
                        return None
                    for idx, arg in enumerate(arg_items):
                        arg = strip_parens(arg)
                        param = sig["params"][idx] if sig else None
                        arg_type = None
                        arg_val = None
                        index_match = index_pattern.fullmatch(arg)
                        if arg.lower() in {"true", "false"}:
                            arg_type = "bool"
                            arg_val = 1 if arg.lower() == "true" else 0
                            c_args.append("1" if arg.lower() == "true" else "0")
                        elif re.fullmatch(r"[0-9]+", arg):
                            arg_type = "i32"
                            arg_val = int(arg)
                            c_args.append(arg)
                        elif index_match:
                            arr_name, idx_token = index_match.groups()
                            if arr_name not in variables or "length" not in variables[arr_name]:
                                return None
                            arr_info = variables[arr_name]
                            arr_len = arr_info["length"]
                            if re.fullmatch(r"[0-9]+", idx_token):
                                if int(idx_token) >= arr_len:
                                    return None
                                idx_c = idx_token
                            elif idx_token in variables and variables[idx_token].get("bound") == ("<", arr_len):
                                idx_c = idx_token
                            else:
                                return None
                            arg_type = arr_info["elem_type"]
                            c_args.append(f"{arr_name}[{idx_c}]")
                        elif arg in variables:
                            arg_type = variables[arg]["type"]
                            c_args.append(arg)
                        else:
                            return None

                        if param:
                            expected = param["type"]
                            if expected == "bool":
                                if arg_type != "bool":
                                    return None
                            elif expected in self.NUMERIC_TYPE_MAP:
                                if arg_type not in self.NUMERIC_TYPE_MAP and arg_type != "i32":
                                    return None
                                if param["bound"] and arg_val is not None:
                                    op, val = param["bound"]
                                    if op == ">" and not (arg_val > val):
                                        return None
                                    if op == "<" and not (arg_val < val):
                                        return None
                                    if op == ">=" and not (arg_val >= val):
                                        return None
                                    if op == "<=" and not (arg_val <= val):
                                        return None
                                    if op == "==" and not (arg_val == val):
                                        return None
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
            param_info = []
            if params_src:
                params = [p.strip() for p in params_src.split(',') if p.strip()]
                for param in params:
                    param_match = param_pattern.fullmatch(param)
                    if not param_match:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    p_name, p_type, bound_op, bound_val = param_match.groups()
                    if bound_op and p_type.lower() == "bool":
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    if p_type.lower() == "bool":
                        p_c_type = "int"
                    elif p_type.lower() in self.NUMERIC_TYPE_MAP:
                        p_c_type = self.NUMERIC_TYPE_MAP[p_type.lower()]
                    else:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    c_params.append(f"{p_c_type} {p_name}")
                    bound = None
                    if bound_op:
                        bound = (bound_op, int(bound_val))
                    param_info.append({"name": p_name, "type": p_type.lower(), "bound": bound})
            param_list = ", ".join(c_params)
            func_sigs[name] = {"params": param_info}

            if ret_type is None or ret_type.lower() == "void":
                if body_str:
                    variables = {}
                    for p in param_info:
                        v_type = p["type"]
                        if v_type == "bool":
                            c_t = "int"
                        else:
                            c_t = self.NUMERIC_TYPE_MAP[v_type]
                        variables[p["name"]] = {
                            "type": v_type,
                            "c_type": c_t,
                            "mutable": False,
                            "bound": p.get("bound"),
                        }
                    lines = compile_block(body_str, 1, variables, func_sigs)
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

