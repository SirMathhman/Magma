import ast
import os
import re
import sys
from pathlib import Path

if __package__ in {None, ""}:
    # Allow running this module directly as a script for quick experiments.
    # Insert the parent directory so that ``magma`` resolves on import.
    sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))
    from magma.numbers import (
        NUMERIC_TYPE_MAP,
        parse_arithmetic,
        parse_numeric_condition,
        range_from_op,
        intersect_range,
        bound_to_range,
        is_subset,
    )
else:
    from .numbers import (
        NUMERIC_TYPE_MAP,
        parse_arithmetic,
        parse_numeric_condition,
        range_from_op,
        intersect_range,
        bound_to_range,
        is_subset,
    )


class Compiler:
    """Minimal compiler skeleton.

    The compiler operates on files to keep the interface simple for now.  This
    mirrors how traditional compilers work and makes future CLI integration
    straightforward.
    """

    NUMERIC_TYPE_MAP = NUMERIC_TYPE_MAP

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
        enums = []
        globals = []
        includes = []
        func_structs = set()
        env_struct_fields = {}
        env_init_emitted = set()

        def build_env_init(name: str, params: list[dict]) -> list[str]:
            """Return lines to initialize the captured environment."""
            indent_str = " " * 4
            env_struct_fields.setdefault(name, [])
            init_lines = []
            if name not in env_init_emitted:
                init_lines.append(f"{indent_str}struct {name}_t this;")
                env_init_emitted.add(name)
            for p in params:
                c_t = p.get("c_type") or c_type_of(p["type"])
                env_struct_fields[name].append((p["name"], c_t))
                init_lines.append(f"{indent_str}this.{p['name']} = {p['name']};")
            return init_lines
        extern_found = False
        header_pattern = re.compile(
            r"fn\s+(\w+)\s*\(\s*(.*?)\s*\)\s*(?::\s*(\w+)\s*)?=>\s*{",
            re.IGNORECASE | re.DOTALL,
        )
        extern_fn_pattern = re.compile(
            r"extern\s+fn\s+(\w+)\s*\(\s*(.*?)\s*\)\s*(?::\s*(\w+)\s*)?;",
            re.IGNORECASE | re.DOTALL,
        )
        param_pattern = re.compile(
            r"(\w+)\s*:\s*((?:\([^\)]*\)\s*=>\s*\w+)|\w+)(?:\s*(<=|>=|<|>|==)\s*([0-9]+))?",
            re.IGNORECASE | re.DOTALL,
        )
        struct_pattern = re.compile(
            r"struct\s+(\w+)\s*{\s*(.*?)\s*}\s*",
            re.IGNORECASE | re.DOTALL,
        )
        generic_struct_pattern = re.compile(
            r"struct\s+(\w+)\s*<\s*(\w+)\s*>\s*{\s*(.*?)\s*}\s*",
            re.IGNORECASE | re.DOTALL,
        )
        generic_class_fn_pattern = re.compile(
            r"class\s+fn\s+(\w+)\s*<\s*(\w+)\s*>\s*\(\s*(.*?)\s*\)\s*=>\s*{",
            re.IGNORECASE | re.DOTALL,
        )
        generic_fn_pattern = re.compile(
            r"fn\s+(\w+)\s*<\s*(\w+)\s*>\s*\(\s*\)\s*(?::\s*(\w+)\s*)?=>\s*{",
            re.IGNORECASE | re.DOTALL,
        )
        class_fn_pattern = re.compile(
            r"class\s+fn\s+(\w+)\s*\(\s*(.*?)\s*\)\s*=>\s*{",
            re.IGNORECASE | re.DOTALL,
        )
        field_pattern = re.compile(
            r"(\w+)\s*:\s*(.+)",
            re.IGNORECASE | re.DOTALL,
        )
        func_type_pattern = re.compile(
            r"\(\s*(.*?)\s*\)\s*=>\s*(\w+)",
            re.IGNORECASE | re.DOTALL,
        )
        let_pattern = re.compile(
            r"let\s+(mut\s+)?(\w+)(?:\s*:\s*(\[[^\]]+\]|[^=;]+))?(?:\s*=\s*(.+?))?\s*;",
            re.IGNORECASE | re.DOTALL,
        )
        assign_pattern = re.compile(r"(\w+)\s*=\s*(.+?)\s*;", re.IGNORECASE | re.DOTALL)
        call_pattern = re.compile(r"(\w+)\s*\((.*?)\)\s*;", re.DOTALL)
        variant_init_pattern = re.compile(r"(\w+)\.(\w+)\s*(?:\((.*?)\))?")
        if_pattern = re.compile(r"if\s*\((.+?)\)\s*{", re.DOTALL)
        while_pattern = re.compile(r"while\s*\((.+?)\)\s*{", re.DOTALL)
        return_pattern = re.compile(r"return(?:\s+(.*?))?\s*;", re.DOTALL)
        break_pattern = re.compile(r"break\s*;")
        continue_pattern = re.compile(r"continue\s*;")
        type_pattern = re.compile(r"type\s+(\w+)\s*=\s*(\w+)\s*;")
        enum_pattern = re.compile(
            r"enum\s+(\w+)\s*{\s*(.*?)\s*}\s*",
            re.DOTALL,
        )
        struct_enum_pattern = re.compile(
            r"struct\s+enum\s+(\w+)\s*{\s*(.*?)\s*}\s*",
            re.DOTALL,
        )
        array_type_pattern = re.compile(
            r"\[\s*(\w+)\s*;\s*([0-9]+)\s*\]",
            re.IGNORECASE,
        )
        array_value_pattern = re.compile(r"\[\s*(.*?)\s*\]", re.DOTALL)
        bounded_type_pattern = re.compile(
            r"(\w+)(?:\s*(<=|>=|<|>|==)\s*([0-9]+|\w+\.length))?",
            re.IGNORECASE,
        )
        index_pattern = re.compile(r"(\w+)\s*\[\s*(.+?)\s*\]")
        import_pattern = re.compile(r"import\s+(\w+)\s*;")

        func_sigs = {}
        type_aliases = {}
        struct_names = {}
        struct_fields = {}
        generic_structs = {}
        generic_classes = {}
        generic_funcs = {}
        struct_instances = {}
        enum_names = {}
        struct_enum_variants = {}
        global_vars = {}

        CANONICAL_TYPE = {
            "bool": "Bool",
            "u8": "U8",
            "u16": "U16",
            "u32": "U32",
            "u64": "U64",
            "usize": "USize",
            "i8": "I8",
            "i16": "I16",
            "i32": "I32",
            "i64": "I64",
        }

        def resolve_type(t: str, type_map: dict | None = None):
            type_map = {k.lower(): v for k, v in (type_map or {}).items()}
            if t.startswith("*"):
                inner = resolve_type(t[1:].strip(), type_map)
                if inner is None:
                    return None
                return f"*{inner}"
            generic = re.fullmatch(r"(\w+)\s*<\s*(\w+)\s*>", t)
            if generic:
                base, arg = generic.groups()
                if base not in generic_structs and base not in generic_classes:
                    return None
                arg_res = resolve_type(arg, type_map)
                if arg_res is None:
                    return None
                if (
                    arg_res not in self.NUMERIC_TYPE_MAP
                    and arg_res != "bool"
                    and arg_res not in struct_names.values()
                    and not arg_res.startswith("*")
                ):
                    return None
                key = (base, arg_res)
                if key not in struct_instances:
                    if base in generic_structs:
                        param_name = generic_structs[base]["param"].lower()
                        fields = generic_structs[base]["fields"]
                    else:
                        param_name = generic_classes[base]["param"].lower()
                        fields = generic_classes[base]["fields"]
                    new_fields = []
                    c_fields = []
                    sub_map = dict(type_map)
                    sub_map[param_name] = arg_res
                    for fname, ftype in fields:
                        base_ftype = resolve_type(ftype, sub_map)
                        if base_ftype is None:
                            return None
                        new_fields.append((fname, base_ftype))
                        c_t = c_type_of(base_ftype)
                        if not c_t:
                            return None
                        c_fields.append(f"{c_t} {fname};")
                    mono = f"{base}_{CANONICAL_TYPE[arg_res]}"
                    struct_instances[key] = mono
                    struct_names[mono.lower()] = mono
                    struct_names[f"{base.lower()}<{arg.lower()}>"] = mono
                    struct_fields[mono] = new_fields
                    if c_fields:
                        joined = "\n    ".join(c_fields)
                        structs.append(f"struct {mono} {{\n    {joined}\n}};\n")
                    else:
                        structs.append(f"struct {mono} {{\n}};\n")
                    if base in generic_classes:
                        c_params = []
                        for fname, ftype in new_fields:
                            c_t = c_type_of(ftype)
                            c_params.append(f"{c_t} {fname}")
                        param_list = ", ".join(c_params)
                        func_lines = [f"struct {mono} {mono}({param_list}) {{"]
                        func_lines.append(f"    struct {mono} this;")
                        for fname, _ in new_fields:
                            func_lines.append(f"    this.{fname} = {fname};")
                        func_lines.append("    return this;")
                        func_lines.append("}")
                        funcs.append("\n".join(func_lines) + "\n")
                return struct_instances[key]

            t = t.lower()
            if t in type_map:
                return type_map[t]
            seen = set()
            while t in type_aliases:
                if t in seen:
                    return None
                seen.add(t)
                t = type_aliases[t].lower()
            if t in struct_names:
                return struct_names[t]
            if t in enum_names:
                return enum_names[t]
            return t

        def c_type_of(base: str):
            if base.startswith("*"):
                inner = c_type_of(base[1:])
                if not inner:
                    return None
                return f"{inner}*"
            if base == "bool":
                return "int"
            if base in self.NUMERIC_TYPE_MAP:
                return self.NUMERIC_TYPE_MAP[base]
            if base in struct_names.values():
                return f"struct {base}"
            return None

        def bool_to_c(val: str) -> str:
            return "1" if val.lower() == "true" else "0"

        def parse_func_type(ftype: str, type_map: dict | None = None):
            match = func_type_pattern.fullmatch(ftype.strip())
            if not match:
                return None
            params_src = match.group(1).strip()
            ret = match.group(2)
            ret_base = resolve_type(ret, type_map) if ret.lower() != "void" else "void"
            if ret_base is None:
                return None
            c_ret = c_type_of(ret_base) if ret_base != "void" else "void"
            if not c_ret:
                return None
            param_bases = []
            c_params = []
            if params_src:
                for p in [pt.strip() for pt in params_src.split(',') if pt.strip()]:
                    base = resolve_type(p, type_map)
                    if base is None:
                        return None
                    c_t = c_type_of(base)
                    if not c_t:
                        return None
                    param_bases.append(base)
                    c_params.append(c_t)
            c_param_list = ", ".join(c_params)
            magma_type = f"fn({', '.join(param_bases)})->{ret_base}"
            c_type = f"{c_ret} (*)({c_param_list})"
            return magma_type, c_type, c_ret, c_param_list

        def emit_return(expr: str | None, indent: str) -> str:
            if expr is None:
                return f"{indent}return;"
            return f"{indent}return {expr};"

        def parse_params(params_src: str, allow_bounds: bool = True, type_map: dict | None = None):
            struct_fields_local = []
            c_fields_local = []
            c_params = []
            param_info = []
            if params_src:
                params = [p.strip() for p in params_src.split(',') if p.strip()]
                for param in params:
                    p_match = param_pattern.fullmatch(param)
                    if not p_match:
                        return None
                    p_name, p_type, bound_op, bound_val = p_match.groups()
                    func_res = parse_func_type(p_type, type_map)
                    if func_res:
                        if bound_op:
                            return None
                        magma_t, c_t, c_ret, c_param_list = func_res
                        struct_fields_local.append((p_name, magma_t))
                        c_fields_local.append(f"{c_ret} (*{p_name})({c_param_list});")
                        c_params.append(f"{c_ret} (*{p_name})({c_param_list})")
                        param_info.append({"name": p_name, "type": magma_t, "c_type": c_t})
                        continue
                    base = resolve_type(p_type, type_map)
                    if base is None or (bound_op and base == "bool"):
                        return None
                    c_type = c_type_of(base)
                    if not c_type:
                        return None
                    struct_fields_local.append((p_name, base))
                    c_fields_local.append(f"{c_type} {p_name};")
                    c_params.append(f"{c_type} {p_name}")
                    bound = None
                    if bound_op:
                        if not allow_bounds:
                            return None
                        bound = (bound_op, int(bound_val))
                    param_info.append({"name": p_name, "type": base, "bound": bound, "c_type": c_type})
            return struct_fields_local, c_fields_local, c_params, param_info

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
                        return text[start + 1: pos], pos + 1
                pos += 1
            return None, start



        def parse_inline_class(expr: str):
            m = re.match(r"\(\s*class\s+fn\s+(\w+)\s*\((.*?)\)\s*=>\s*{", expr, re.DOTALL)
            if not m:
                return None
            name, params_src = m.group(1), m.group(2)
            body, end = extract_braced_block(expr, m.end() - 1)
            if body is None:
                return None
            rest = expr[end:].strip()
            call_match = re.fullmatch(r"\)\s*\((.*?)\)", rest, re.DOTALL)
            if not call_match:
                return None
            args_src = call_match.group(1).strip()
            return name, params_src.strip(), body, args_src

        def analyze_expr(expr: str, variables: dict, func_sigs: dict):
            field_refs = set()

            ref_match = re.fullmatch(r"&\s*(\w+)", expr)
            if ref_match:
                name = ref_match.group(1)
                if name not in variables:
                    return None
                base_t = variables[name]["type"]
                return {"type": f"*{base_t}", "value": None, "c_expr": f"&{name}"}

            deref_match = re.fullmatch(r"\*\s*(\w+)", expr)
            if deref_match:
                name = deref_match.group(1)
                if name not in variables:
                    return None
                base_t = variables[name]["type"]
                if not base_t.startswith("*"):
                    return None
                inner = base_t[1:]
                return {"type": inner, "value": None, "c_expr": f"*{name}"}

            struct_lit_field = re.fullmatch(
                r"\(?\s*(\w+)\s*{\s*(.*?)\s*}\s*\)?\s*\.\s*(\w+)",
                expr,
                re.DOTALL,
            )
            if struct_lit_field:
                sname = struct_lit_field.group(1)
                vals = [v.strip() for v in struct_lit_field.group(2).split(',') if v.strip()]
                field = struct_lit_field.group(3)
                base = resolve_type(sname)
                if base not in struct_fields:
                    return None
                fields = struct_fields[base]
                if len(vals) != len(fields):
                    return None
                for val_token, (fname, ftype) in zip(vals, fields):
                    if fname != field:
                        continue
                    if ftype == "bool":
                        if val_token.lower() not in {"true", "false"}:
                            return None
                        c_val = "1" if val_token.lower() == "true" else "0"
                        return {"type": "bool", "value": 1 if c_val == "1" else 0, "c_expr": c_val}
                    if not re.fullmatch(r"[0-9]+", val_token):
                        return None
                    return {"type": "i32", "value": int(val_token), "c_expr": val_token}
                return None

            expr_py = re.sub(r"\btrue\b", "True", expr, flags=re.IGNORECASE)
            expr_py = re.sub(r"\bfalse\b", "False", expr_py, flags=re.IGNORECASE)
            expr_c = re.sub(r"\btrue\b", "1", expr, flags=re.IGNORECASE)
            expr_c = re.sub(r"\bfalse\b", "0", expr_c, flags=re.IGNORECASE)
            try:
                node = ast.parse(expr_py, mode="eval").body
            except Exception:
                return None

            def walk(n):
                if isinstance(n, ast.BinOp) and isinstance(
                        n.op, (ast.Add, ast.Sub, ast.Mult, ast.Div)
                ):
                    l = walk(n.left)
                    r = walk(n.right)
                    if l is None or r is None or l["type"] != "i32" or r["type"] != "i32":
                        return None
                    val = None
                    if l["value"] is not None and r["value"] is not None:
                        if isinstance(n.op, ast.Add):
                            val = l["value"] + r["value"]
                        elif isinstance(n.op, ast.Sub):
                            val = l["value"] - r["value"]
                        elif isinstance(n.op, ast.Mult):
                            val = l["value"] * r["value"]
                        elif isinstance(n.op, ast.Div):
                            val = l["value"] // r["value"]
                    return {"type": "i32", "value": val}
                if isinstance(n, ast.UnaryOp) and isinstance(n.op, (ast.UAdd, ast.USub)):
                    inner = walk(n.operand)
                    if inner is None or inner["type"] != "i32":
                        return None
                    val = None
                    if inner["value"] is not None:
                        val = inner["value"]
                        if isinstance(n.op, ast.USub):
                            val = -val
                    return {"type": "i32", "value": val}
                if isinstance(n, ast.Constant):
                    if isinstance(n.value, bool):
                        return {"type": "bool", "value": int(n.value)}
                    if isinstance(n.value, int):
                        return {"type": "i32", "value": int(n.value)}
                    return None
                if isinstance(n, ast.Name):
                    if n.id not in variables:
                        if "this" in variables:
                            this_type = variables["this"]["type"]
                            if this_type in struct_fields:
                                for fname, ftype in struct_fields[this_type]:
                                    if fname == n.id:
                                        field_refs.add(fname)
                                        if ftype == "bool":
                                            return {"type": "bool", "value": None}
                                        if ftype in self.NUMERIC_TYPE_MAP or ftype == "i32":
                                            return {"type": "i32", "value": None}
                                        if ftype in struct_fields or ftype in struct_names.values():
                                            return {"type": ftype, "value": None}
                        return None
                    v = variables[n.id]
                    t = v["type"]
                    if t == "bool":
                        return {"type": "bool", "value": None}
                    if t in self.NUMERIC_TYPE_MAP or t == "i32":
                        return {"type": "i32", "value": None}
                    if t in struct_fields or t.endswith("_t") or t in struct_names.values():
                        return {"type": t, "value": None}
                    return None
                if isinstance(n, ast.Attribute) and isinstance(n.value, ast.Name):
                    base_name = n.value.id
                    if base_name not in variables:
                        return None
                    base_type = variables[base_name]["type"]
                    if base_type not in struct_fields:
                        return None
                    for fname, ftype in struct_fields[base_type]:
                        if fname == n.attr:
                            if ftype == "bool":
                                return {"type": "bool", "value": None}
                            if ftype in self.NUMERIC_TYPE_MAP or ftype == "i32":
                                return {"type": "i32", "value": None}
                            return None
                    return None
                if isinstance(n, ast.Call) and isinstance(n.func, ast.Name):
                    name = n.func.id
                    sig = func_sigs.get(name)
                    if sig is None:
                        return None
                    if len(n.args) != len(sig["params"]):
                        return None
                    for arg_node, param in zip(n.args, sig["params"]):
                        res = walk(arg_node)
                        if res is None:
                            return None
                        if param["type"] == "bool" and res["type"] != "bool":
                            return None
                        if param["type"] != "bool" and res["type"] != "i32":
                            return None
                    ret = sig.get("ret", "void")
                    if ret == "bool":
                        return {"type": "bool", "value": None}
                    if ret in self.NUMERIC_TYPE_MAP or ret == "i32":
                        return {"type": "i32", "value": None}
                    if ret in struct_fields or ret in struct_names.values() or ret.endswith("_t"):
                        return {"type": ret, "value": None}
                    return None
                return None

            result = walk(node)
            if result is None:
                return None
            for fname in field_refs:
                expr_c_local = re.sub(fr"\b{fname}\b", f"this.{fname}", expr_c)
                expr_c = expr_c_local
            result["c_expr"] = expr_c
            return result

        def parse_bool_condition(cond: str):
            m = re.fullmatch(r"(\w+)\s*==\s*(true|false)", cond, re.IGNORECASE)
            if m:
                return m.group(1), m.group(2).lower() == "true"
            m = re.fullmatch(r"(true|false)\s*==\s*(\w+)", cond, re.IGNORECASE)
            if m:
                return m.group(2), m.group(1).lower() == "true"
            return None



        def condition_to_c(cond: str, variables: dict):
            comp_match = re.match(r"(.+?)\s*(==|<=|>=|<|>)\s*(.+)", cond)
            if cond.lower() in {"true", "false"}:
                return "1" if cond.lower() == "true" else "0"
            if comp_match:
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

                return f"{to_c(left, l_type)} {op} {to_c(right, r_type)}"
            return cond

        def compile_block(
            block: str,
            indent: int,
            variables: dict,
            func_sigs: dict,
            conditions: dict,
            ret_holder: dict,
            func_name: str,
            capture_env: bool = False,
        ):
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
                    sub_lines = compile_block(inner, indent + 1, variables, func_sigs, conditions, ret_holder, func_name, False)
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

                    cond_c = condition_to_c(condition, variables)
                    if cond_c is None:
                        return None
                    new_conditions = dict(conditions)
                    num_cond = parse_numeric_condition(condition)
                    bool_cond = parse_bool_condition(condition)
                    if num_cond:
                        var, op, val = num_cond
                        rng = range_from_op(op, val)
                        if var in new_conditions and isinstance(new_conditions[var], tuple):
                            rng = intersect_range(new_conditions[var], rng)
                            if rng is None:
                                return None
                        elif var in new_conditions and not isinstance(new_conditions[var], tuple):
                            return None
                        new_conditions[var] = rng
                    elif bool_cond:
                        var, val = bool_cond
                        if var in new_conditions:
                            if isinstance(new_conditions[var], tuple) or new_conditions[var] != val:
                                return None
                        new_conditions[var] = val
                    sub_lines = compile_block(inner, indent + 1, variables, func_sigs, new_conditions, ret_holder, func_name, False)
                    if sub_lines is None:
                        return None
                    lines.append(f"{indent_str}if ({cond_c}) {{")
                    lines.extend(sub_lines)
                    lines.append(f"{indent_str}}}")
                    pos2 = new_pos
                    continue

                while_match = while_pattern.match(block, pos2)
                if while_match:
                    condition = strip_parens(while_match.group(1))
                    inner, new_pos = extract_braced_block(block, while_match.end() - 1)
                    if inner is None:
                        return None

                    cond_c = condition_to_c(condition, variables)
                    if cond_c is None:
                        return None

                    sub_lines = compile_block(inner, indent + 1, variables, func_sigs, conditions, ret_holder, func_name, False)
                    if sub_lines is None:
                        return None
                    lines.append(f"{indent_str}while ({cond_c}) {{")
                    lines.extend(sub_lines)
                    lines.append(f"{indent_str}}}")
                    pos2 = new_pos
                    continue

                break_match = break_pattern.match(block, pos2)
                if break_match:
                    lines.append(f"{indent_str}break;")
                    pos2 = break_match.end()
                    continue

                cont_match = continue_pattern.match(block, pos2)
                if cont_match:
                    lines.append(f"{indent_str}continue;")
                    pos2 = cont_match.end()
                    continue

                nested_match = header_pattern.match(block, pos2)
                if nested_match:
                    if func_name not in func_structs:
                        func_structs.add(func_name)
                    inner_name = nested_match.group(1)
                    params_src = nested_match.group(2).strip()
                    inner_ret = nested_match.group(3)
                    inner_body, new_pos = extract_braced_block(block, nested_match.end() - 1)
                    if inner_body is None:
                        return None

                    new_name = f"{inner_name}_{func_name}"
                    res = parse_params(params_src)
                    if res is None:
                        return None
                    _, _, c_params, param_info = res
                    this_param = f"struct {func_name}_t this"
                    c_params = [this_param] + c_params
                    param_list = ", ".join(c_params)
                    ret_resolved = resolve_type(inner_ret) if inner_ret else "void"
                    if inner_ret and ret_resolved is None:
                        return None
                    func_sigs[inner_name] = {"params": param_info, "ret": ret_resolved, "c_name": new_name}

                    variables_inner = {
                        "this": {
                            "type": f"{func_name}_t",
                            "c_type": f"struct {func_name}_t",
                            "mutable": False,
                            "bound": None,
                        }
                    }
                    for p in param_info:
                        v_type = p["type"]
                        c_t = p.get("c_type") or c_type_of(v_type)
                        variables_inner[p["name"]] = {
                            "type": v_type,
                            "c_type": c_t,
                            "mutable": False,
                            "bound": p.get("bound"),
                        }

                    ret_holder_inner = {"type": ret_resolved}
                    inner_lines = compile_block(inner_body, 1, variables_inner, func_sigs, {}, ret_holder_inner, new_name, False)
                    if inner_lines is None:
                        return None
                    body_text = "\n".join(inner_lines)
                    if body_text:
                        body_text += "\n"
                    final_inner_ret = ret_holder_inner.get("type") or "void"
                    c_ret = c_type_of(final_inner_ret) or final_inner_ret
                    func_sigs[inner_name]["ret"] = final_inner_ret
                    funcs.append(f"{c_ret} {new_name}({param_list}) {{\n{body_text}}}\n")
                    pos2 = new_pos
                    continue

                let_match = let_pattern.match(block, pos2)
                if let_match:
                    mutable = let_match.group(1) is not None
                    var_name = let_match.group(2)
                    var_type = let_match.group(3)
                    raw_value = let_match.group(4)
                    value = strip_parens(raw_value) if raw_value else None
                    if (
                        var_type
                        and raw_value
                        and var_type.strip().endswith(")")
                        and raw_value.strip().startswith(">")
                    ):
                        var_type = f"{var_type.strip()} => {raw_value.strip()[1:].strip()}"
                        value = None

                    struct_init = None
                    if value is not None:
                        struct_init = re.fullmatch(r"(\w+)\s*{\s*(.*?)\s*}", value, re.DOTALL)

                    var_type = var_type.strip() if var_type else None

                    if capture_env and indent == 1 and not struct_init:
                        if func_name not in env_init_emitted:
                            lines.append(f"{indent_str}struct {func_name}_t this;")
                            env_init_emitted.add(func_name)

                        c_val = None
                        if var_type is None:
                            if value is None:
                                return None
                            if value.lower() in {"true", "false"}:
                                base = "bool"
                                c_val = bool_to_c(value)
                            elif re.fullmatch(r"[0-9]+", value):
                                base = "i32"
                                c_val = value
                            else:
                                return None
                            c_type = c_type_of(base)
                        else:
                            base = resolve_type(var_type)
                            c_type = c_type_of(base)
                            if not c_type:
                                return None
                            if value is not None:
                                if base == "bool" and value.lower() in {"true", "false"}:
                                    c_val = bool_to_c(value)
                                elif base in self.NUMERIC_TYPE_MAP and re.fullmatch(r"[0-9]+", value):
                                    c_val = value
                                else:
                                    return None

                        env_struct_fields.setdefault(func_name, []).append((var_name, c_type))
                        variables[var_name] = {"type": base, "c_type": c_type, "mutable": mutable, "bound": None}
                        if c_val is not None:
                            lines.append(f"{indent_str}this.{var_name} = {c_val};")
                        pos2 = let_match.end()
                        continue

                    magma_bound = None
                    array_type = None

                    if var_type and var_type.lower() == "void":
                        return None

                    if struct_init:
                        init_name = struct_init.group(1)
                        vals = [v.strip() for v in struct_init.group(2).split(',') if v.strip()]
                        if var_type:
                            base = resolve_type(var_type)
                            vm = re.fullmatch(r"(\w+)\s*<\s*(\w+)\s*>", var_type)
                            expected = vm.group(1) if vm else var_type
                            if init_name != expected:
                                return None
                            base_init = base
                        else:
                            base_init = resolve_type(init_name)
                            if base_init not in struct_fields:
                                return None
                            base = base_init
                        fields = struct_fields[base_init]
                        if len(vals) != len(fields):
                            return None
                        c_type = f"struct {base}"
                        lines.append(f"{indent_str}{c_type} {var_name};")
                        magma_type = base
                        for val_item, (fname, ftype) in zip(vals, fields):
                            if ftype == "bool":
                                if val_item.lower() in {"true", "false"}:
                                    c_val = "1" if val_item.lower() == "true" else "0"
                                elif val_item in variables and variables[val_item]["type"] == "bool":
                                    c_val = val_item
                                else:
                                    expr_info = analyze_expr(val_item, variables, func_sigs)
                                    if not expr_info or expr_info["type"] != "bool":
                                        return None
                                    c_val = expr_info["c_expr"]
                            else:
                                if re.fullmatch(r"[0-9]+", val_item) or parse_arithmetic(val_item) is not None:
                                    c_val = val_item
                                elif val_item in variables and (
                                        variables[val_item]["type"] in self.NUMERIC_TYPE_MAP or variables[val_item][
                                    "type"] == "i32"):
                                    c_val = val_item
                                else:
                                    expr_info = analyze_expr(val_item, variables, func_sigs)
                                    if not expr_info or expr_info["type"] != "i32":
                                        return None
                                    c_val = expr_info["c_expr"]
                            lines.append(f"{indent_str}{var_name}.{fname} = {c_val};")
                        variables[var_name] = {
                            "type": magma_type,
                            "c_type": c_type,
                            "mutable": mutable,
                            "bound": None,
                        }
                        pos2 = let_match.end()
                        continue

                    variant_init = None
                    if value is not None:
                        variant_init = variant_init_pattern.fullmatch(value)

                    if variant_init:
                        base_name, vname, params_src = variant_init.groups()
                        params_src = params_src.strip() if params_src else ""
                        params = [p.strip() for p in params_src.split(',') if p.strip()] if params_src else []
                        if var_type:
                            if '.' in var_type:
                                vt_base, vt_var = var_type.split('.', 1)
                                if vt_var != vname:
                                    return None
                            else:
                                vt_base = var_type
                            base = resolve_type(vt_base)
                            if base is None:
                                return None
                            if resolve_type(base_name) != base:
                                return None
                        else:
                            base = resolve_type(base_name)
                            if base is None:
                                return None
                        if base not in struct_enum_variants or vname not in struct_enum_variants[base]:
                            return None
                        fields = struct_fields.get(vname, [])
                        if len(params) != len(fields):
                            return None
                        c_type = f"struct {base}"
                        lines.append(f"{indent_str}{c_type} {var_name};")
                        lines.append(f"{indent_str}{var_name}.tag = {vname};")
                        for val_item, (fname, ftype) in zip(params, fields):
                            if ftype == "bool":
                                if val_item.lower() in {"true", "false"}:
                                    c_val = "1" if val_item.lower() == "true" else "0"
                                elif val_item in variables and variables[val_item]["type"] == "bool":
                                    c_val = val_item
                                else:
                                    expr_info = analyze_expr(val_item, variables, func_sigs)
                                    if not expr_info or expr_info["type"] != "bool":
                                        return None
                                    c_val = expr_info["c_expr"]
                            else:
                                if re.fullmatch(r"[0-9]+", val_item) or parse_arithmetic(val_item) is not None:
                                    c_val = val_item
                                elif val_item in variables and (
                                    variables[val_item]["type"] in self.NUMERIC_TYPE_MAP or variables[val_item]["type"] == "i32"
                                ):
                                    c_val = val_item
                                else:
                                    expr_info = analyze_expr(val_item, variables, func_sigs)
                                    if not expr_info or expr_info["type"] != "i32":
                                        return None
                                    c_val = expr_info["c_expr"]
                            lines.append(f"{indent_str}{var_name}.data.{vname}.{fname} = {c_val};")
                        magma_type = base
                        variables[var_name] = {
                            "type": magma_type,
                            "c_type": c_type,
                            "mutable": mutable,
                            "bound": None,
                        }
                        pos2 = let_match.end()
                        continue

                    if value is None:
                        if var_type is None:
                            return None
                        func_match = re.fullmatch(r"\(\s*(.*?)\s*\)\s*=>\s*(\w+)", var_type)
                        if func_match:
                            params_src = func_match.group(1).strip()
                            ret = func_match.group(2)
                            ret_base = resolve_type(ret) if ret.lower() != "void" else "void"
                            if ret_base is None:
                                return None
                            if ret_base == "void":
                                c_ret = "void"
                            else:
                                c_ret = c_type_of(ret_base)
                                if not c_ret:
                                    return None

                            param_bases = []
                            c_params = []
                            if params_src:
                                for p in [pt.strip() for pt in params_src.split(',') if pt.strip()]:
                                    base = resolve_type(p)
                                    if base is None:
                                        return None
                                    c_t = c_type_of(base)
                                    if not c_t:
                                        return None
                                    param_bases.append(base)
                                    c_params.append(c_t)
                            c_param_list = ", ".join(c_params)
                            lines.append(
                                f"{indent_str}{c_ret} (*{var_name})({c_param_list});"
                            )
                            magma_type = f"fn({', '.join(param_bases)})->{ret_base}"
                            variables[var_name] = {
                                "type": magma_type,
                                "c_type": f"{c_ret} (*)({c_param_list})",
                                "mutable": mutable,
                                "bound": magma_bound,
                            }
                            pos2 = let_match.end()
                            continue
                        ptr_type = var_type.strip().startswith("*")
                        if ptr_type:
                            inner_base = resolve_type(var_type.strip()[1:])
                            if inner_base is None:
                                return None
                            c_inner = c_type_of(inner_base)
                            if not c_inner:
                                return None
                            c_type = f"{c_inner}*"
                            magma_type = f"*{inner_base}"
                            lines.append(f"{indent_str}{c_type} {var_name};")
                            variables[var_name] = {
                                "type": magma_type,
                                "c_type": c_type,
                                "mutable": mutable,
                                "bound": magma_bound,
                            }
                            pos2 = let_match.end()
                            continue

                        array_type = array_type_pattern.fullmatch(var_type)
                        if array_type:
                            elem_type = array_type.group(1)
                            elem_base = resolve_type(elem_type)
                            if elem_base not in self.NUMERIC_TYPE_MAP and elem_base != "bool":
                                return None
                            size = int(array_type.group(2))
                            c_type = c_type_of(elem_base)
                            lines.append(f"{indent_str}{c_type} {var_name}[{size}];")
                            magma_type = f"[{elem_type};{size}]"
                            variables[var_name] = {
                                "type": magma_type,
                                "c_type": c_type,
                                "mutable": mutable,
                                "bound": magma_bound,
                                "length": size,
                                "elem_type": elem_base,
                            }
                            pos2 = let_match.end()
                            continue
                        base = resolve_type(var_type)
                        if base in struct_names.values():
                            c_type = f"struct {base}"
                            magma_type = base
                            lines.append(f"{indent_str}{c_type} {var_name};")
                        elif bounded_type_match := bounded_type_pattern.fullmatch(var_type):
                            base_type = bounded_type_match.group(1)
                            base_res = resolve_type(base_type)
                            bound_op = bounded_type_match.group(2)
                            bound_val = bounded_type_match.group(3)
                            if base_res not in self.NUMERIC_TYPE_MAP:
                                return None
                            c_type = c_type_of(base_res)
                            magma_type = base_res
                            bound = None
                            if bound_op:
                                if bound_val.endswith(".length"):
                                    arr_name = bound_val[: -len(".length")]
                                    if arr_name not in variables or "length" not in variables[arr_name]:
                                        return None
                                    bound = (bound_op, variables[arr_name]["length"])
                                else:
                                    bound = (bound_op, int(bound_val))
                            lines.append(f"{indent_str}{c_type} {var_name};")
                            magma_bound = bound
                        elif base == "bool" or base in self.NUMERIC_TYPE_MAP:
                            c_type = c_type_of(base)
                            magma_type = base
                            lines.append(f"{indent_str}{c_type} {var_name};")
                        elif base in struct_names.values():
                            c_type = f"struct {base}"
                            magma_type = base
                            lines.append(f"{indent_str}{c_type} {var_name};")
                        else:
                            return None
                    elif var_type is None:
                        index_match = index_pattern.fullmatch(value)
                        if value.lower() in {"true", "false"}:
                            c_value = bool_to_c(value)
                            c_type = c_type_of("bool")
                            magma_type = "bool"
                        elif re.fullmatch(r"[0-9]+", value) or parse_arithmetic(value) is not None:
                            c_value = value
                            c_type = c_type_of("i32")
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
                            expr_info = analyze_expr(value, variables, func_sigs)
                            if not expr_info or expr_info["type"] != "i32":
                                return None
                            c_value = expr_info["c_expr"]
                            c_type = c_type_of("i32")
                            magma_type = "i32"
                        lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                    else:
                        if var_type.strip().startswith("*"):
                            inner_base = resolve_type(var_type.strip()[1:])
                            if inner_base is None:
                                return None
                            c_inner = c_type_of(inner_base)
                            if not c_inner:
                                return None
                            if not value.startswith("&") or value[1:].strip() not in variables:
                                expr_info = analyze_expr(value, variables, func_sigs)
                                if not expr_info or expr_info["type"] != f"*{inner_base}":
                                    return None
                                c_value = expr_info["c_expr"]
                            else:
                                ref = value[1:].strip()
                                if variables[ref]["type"] != inner_base:
                                    return None
                                c_value = f"&{ref}"
                            c_type = f"{c_inner}*"
                            lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                            magma_type = f"*{inner_base}"
                            variables[var_name] = {
                                "type": magma_type,
                                "c_type": c_type,
                                "mutable": mutable,
                                "bound": magma_bound,
                            }
                            pos2 = let_match.end()
                            continue

                        array_type = array_type_pattern.fullmatch(var_type)
                        if array_type:
                            elem_type = array_type.group(1)
                            elem_base = resolve_type(elem_type)
                            if elem_base not in self.NUMERIC_TYPE_MAP and elem_base != "bool":
                                return None
                            size = int(array_type.group(2))
                            value_match = array_value_pattern.fullmatch(value)
                            if not value_match:
                                return None
                            elems = [v.strip() for v in value_match.group(1).split(',') if v.strip()]
                            if len(elems) != size:
                                return None
                            c_elems = []
                            if elem_base == "bool":
                                for val in elems:
                                    if val.lower() not in {"true", "false"}:
                                        return None
                                    c_elems.append(bool_to_c(val))
                                c_type = c_type_of(elem_base)
                            else:
                                if elem_base not in self.NUMERIC_TYPE_MAP:
                                    return None
                                for val in elems:
                                    if not re.fullmatch(r"[0-9]+", val):
                                        return None
                                    c_elems.append(val)
                                c_type = c_type_of(elem_base)
                            lines.append(f"{indent_str}{c_type} {var_name}[] = {{{', '.join(c_elems)}}};")
                            magma_type = f"[{elem_type};{size}]"
                            variables[var_name] = {
                                "type": magma_type,
                                "c_type": c_type,
                                "mutable": mutable,
                                "bound": magma_bound,
                                "length": size,
                                "elem_type": elem_base,
                            }
                            pos2 = let_match.end()
                            continue
                        base = resolve_type(var_type)
                        if base == "bool":
                            if value.lower() in {"true", "false"}:
                                c_value = bool_to_c(value)
                            elif value in variables and variables[value]["type"] == "bool":
                                c_value = value
                            else:
                                return None
                            c_type = c_type_of(base)
                            lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                            magma_type = "bool"
                        elif bounded_type_match := bounded_type_pattern.fullmatch(var_type):
                            base_type = bounded_type_match.group(1)
                            base_res = resolve_type(base_type)
                            bound_op = bounded_type_match.group(2)
                            bound_val = bounded_type_match.group(3)
                            if base_res not in self.NUMERIC_TYPE_MAP:
                                return None
                            c_type = c_type_of(base_res)
                            magma_type = base_res
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
                                eff_range = bound_to_range(variables[value].get("bound"))
                                cond_range = conditions.get(value)
                                if isinstance(cond_range, tuple):
                                    eff_range = intersect_range(eff_range, cond_range)
                                    if eff_range is None:
                                        return None
                                if bound:
                                    required = range_from_op(bound[0], bound[1])
                                    if not is_subset(eff_range, required):
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
                            if arr_info["elem_type"] != base:
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
                            magma_type = base
                        elif base in self.NUMERIC_TYPE_MAP:
                            c_type = c_type_of(base)
                            if re.fullmatch(r"[0-9]+", value) or parse_arithmetic(value) is not None:
                                c_value = value
                            elif value in variables and variables[value]["type"] in self.NUMERIC_TYPE_MAP:
                                c_value = value
                            elif (dref := re.fullmatch(r"\*\s*(\w+)", value)):
                                name = dref.group(1)
                                if name not in variables:
                                    return None
                                ptr_type = variables[name]["type"]
                                if not ptr_type.startswith("*"):
                                    return None
                                if ptr_type[1:] != base:
                                    return None
                                c_value = f"*{name}"
                            else:
                                expr_info = analyze_expr(value, variables, func_sigs)
                                if not expr_info or expr_info["type"] != "i32":
                                    return None
                                c_value = expr_info["c_expr"]
                            lines.append(f"{indent_str}{c_type} {var_name} = {c_value};")
                            magma_type = base
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
                        variables[var_name]["elem_type"] = elem_base

                    pos2 = let_match.end()
                    continue

                return_match = return_pattern.match(block, pos2)
                if return_match:
                    ret_val = return_match.group(1)
                    current = ret_holder.get("type")
                    if current is None:
                        if ret_val is None:
                            ret_holder["type"] = "void"
                            lines.append(emit_return(None, indent_str))
                        else:
                            val = strip_parens(ret_val)
                            expr_info = analyze_expr(val, variables, func_sigs)
                            if not expr_info:
                                return None
                            ret_holder["type"] = expr_info["type"]
                            lines.append(emit_return(expr_info['c_expr'], indent_str))
                    else:
                        if ret_val is None and current != "void":
                            return None
                        if ret_val is None:
                            lines.append(emit_return(None, indent_str))
                        else:
                            val = strip_parens(ret_val)
                            expr_info = analyze_expr(val, variables, func_sigs)
                            if not expr_info:
                                return None
                            if current == "bool":
                                if expr_info["type"] != "bool":
                                    return None
                            elif current in self.NUMERIC_TYPE_MAP or current == "i32":
                                if expr_info["type"] not in self.NUMERIC_TYPE_MAP and expr_info["type"] != "i32":
                                    return None
                            else:
                                if expr_info["type"] != current:
                                    return None
                            lines.append(emit_return(expr_info['c_expr'], indent_str))
                    pos2 = return_match.end()
                    continue

                assign_match = assign_pattern.match(block, pos2)
                if assign_match:
                    var_name = assign_match.group(1)
                    value = strip_parens(assign_match.group(2))

                    if var_name not in variables or not variables[var_name]["mutable"]:
                        return None

                    var_type = variables[var_name]["type"]
                    base = var_type
                    index_match = index_pattern.fullmatch(value)
                    if base == "bool":
                        if value.lower() not in {"true", "false"}:
                            return None
                        c_value = bool_to_c(value)
                    elif base in self.NUMERIC_TYPE_MAP or base == "i32":
                        if re.fullmatch(r"[0-9]+", value) or parse_arithmetic(value) is not None:
                            c_value = value
                        elif index_match:
                            arr_name, idx_token = index_match.groups()
                            idx_token = strip_parens(idx_token)
                            if arr_name not in variables or "length" not in variables[arr_name]:
                                return None
                            arr_info = variables[arr_name]
                            if arr_info["elem_type"] != base:
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
                                expr_info = analyze_expr(value, variables, func_sigs)
                                if not expr_info or expr_info["type"] != "i32":
                                    return None
                                c_value = expr_info["c_expr"]
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
                            c_args.append(bool_to_c(arg))
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
                            expr_info = analyze_expr(arg, variables, func_sigs)
                            if not expr_info:
                                return None
                            arg_type = expr_info["type"]
                            arg_val = expr_info["value"]
                            c_args.append(expr_info["c_expr"])

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
                    c_name = func_sigs.get(name, {}).get("c_name", name)
                    env_name = func_name.split("_")[-1]
                    if c_name != name and c_name.endswith(f"_{env_name}"):
                        arg_str = ", ".join(["this"] + c_args)
                    else:
                        arg_str = ", ".join(c_args)
                    lines.append(f"{indent_str}{c_name}({arg_str});")
                    pos2 = call_match.end()
                    continue

                return None

            return lines

        def process_callable(current_pos: int):
            """Handle functions, generic functions, classes, and generic classes."""

            match = generic_class_fn_pattern.match(source, current_pos)
            if match:
                name = match.group(1)
                param = match.group(2)
                params_src = match.group(3).strip()
                body_str, new_pos = extract_braced_block(source, match.end() - 1)
                if body_str is None or body_str.strip():
                    Path(output_path).write_text(f"compiled: {source}")
                    return -1
                generic_classes[name] = {"param": param, "fields": []}
                if params_src:
                    params = [p.strip() for p in params_src.split(',') if p.strip()]
                    for param_item in params:
                        p_match = param_pattern.fullmatch(param_item)
                        if not p_match or p_match.group(3):
                            Path(output_path).write_text(f"compiled: {source}")
                            return -1
                        fname, ftype = p_match.group(1), p_match.group(2)
                        if ftype == param:
                            generic_classes[name]["fields"].append((fname, param))
                        else:
                            base = resolve_type(ftype, {param.lower(): param.lower()})
                            if base is None:
                                Path(output_path).write_text(f"compiled: {source}")
                                return -1
                            generic_classes[name]["fields"].append((fname, ftype))
                return new_pos

            match = generic_fn_pattern.match(source, current_pos)
            if match:
                name = match.group(1)
                param = match.group(2)
                ret_type = match.group(3)
                body_str, new_pos = extract_braced_block(source, match.end() - 1)
                if body_str is None or body_str.strip() or ret_type:
                    Path(output_path).write_text(f"compiled: {source}")
                    return -1
                generic_funcs[name] = {"param": param}
                return new_pos

            match = class_fn_pattern.match(source, current_pos)
            if match:
                name = match.group(1)
                params_src = match.group(2).strip()
                body_str, new_pos = extract_braced_block(source, match.end() - 1)
                if body_str is None:
                    Path(output_path).write_text(f"compiled: {source}")
                    return -1
                if name in struct_fields:
                    Path(output_path).write_text(f"compiled: {source}")
                    return -1
                struct_names[name.lower()] = name
                struct_fields[name] = []
                res = parse_params(params_src, allow_bounds=False)
                if res is None:
                    Path(output_path).write_text(f"compiled: {source}")
                    return -1
                struct_fields[name], c_fields, c_params, param_info = res
                if c_fields:
                    joined = "\n    ".join(c_fields)
                    structs.append(f"struct {name} {{\n    {joined}\n}};\n")
                else:
                    structs.append(f"struct {name} {{\n}};\n")
                func_lines = [f"struct {name} {name}({', '.join(c_params)}) {{"]
                func_lines.append(f"    struct {name} this;")
                for fname, _ in struct_fields[name]:
                    func_lines.append(f"    this.{fname} = {fname};")
                func_lines.append(emit_return("this", "    "))
                func_lines.append("}")
                constructor_code = "\n".join(func_lines) + "\n"
                func_sigs[name] = {"params": param_info, "ret": name, "c_name": name}

                pos2 = 0
                while pos2 < len(body_str):
                    ws = re.match(r"\s*", body_str[pos2:])
                    pos2 += ws.end()
                    if pos2 >= len(body_str):
                        break
                    method_match = header_pattern.match(body_str, pos2)
                    if not method_match:
                        Path(output_path).write_text(f"compiled: {source}")
                        return -1
                    m_name = method_match.group(1)
                    params_src = method_match.group(2).strip()
                    m_ret = method_match.group(3)
                    m_body, new_pos_m = extract_braced_block(body_str, method_match.end() - 1)
                    if m_body is None:
                        Path(output_path).write_text(f"compiled: {source}")
                        return -1
                    new_name = f"{m_name}_{name}"
                    c_params_m = [f"struct {name} this"]
                    res = parse_params(params_src)
                    if res is None:
                        Path(output_path).write_text(f"compiled: {source}")
                        return -1
                    _, _, sub_params, param_info_m = res
                    c_params_m += sub_params
                    param_list_m = ", ".join(c_params_m)
                    ret_resolved = resolve_type(m_ret) if m_ret else None
                    if m_ret and ret_resolved is None:
                        Path(output_path).write_text(f"compiled: {source}")
                        return -1
                    func_sigs[m_name] = {"params": param_info_m, "ret": ret_resolved or "void", "c_name": new_name}

                    variables_m = {
                        "this": {
                            "type": name,
                            "c_type": f"struct {name}",
                            "mutable": False,
                            "bound": None,
                        }
                    }
                    for p in param_info_m:
                        v_type = p["type"]
                        c_t = p.get("c_type") or c_type_of(v_type)
                        variables_m[p["name"]] = {
                            "type": v_type,
                            "c_type": c_t,
                            "mutable": False,
                            "bound": p.get("bound"),
                        }

                    ret_holder_m = {"type": ret_resolved}
                    method_lines = compile_block(m_body, 1, variables_m, func_sigs, {}, ret_holder_m, new_name, False)
                    if method_lines is None:
                        Path(output_path).write_text(f"compiled: {source}")
                        return -1
                    body_text = "\n".join(method_lines)
                    if body_text:
                        body_text += "\n"
                    final_ret = ret_holder_m.get("type") or "void"
                    if final_ret == "void":
                        c_ret = "void"
                    else:
                        c_ret = c_type_of(final_ret)
                        if not c_ret:
                            Path(output_path).write_text(f"compiled: {source}")
                            return -1
                    funcs.append(f"{c_ret} {new_name}({param_list_m}) {{\n{body_text}}}\n")
                    pos2 = new_pos_m

                remaining = body_str[pos2:].strip()
                if remaining:
                    Path(output_path).write_text(f"compiled: {source}")
                    return -1
                funcs.append(constructor_code)
                return new_pos

            match = header_pattern.match(source, current_pos)
            if not match:
                return None
            name = match.group(1)
            params_src = match.group(2).strip()
            ret_type = match.group(3)
            body_str, new_pos = extract_braced_block(source, match.end() - 1)
            if body_str is None:
                Path(output_path).write_text(f"compiled: {source}")
                return -1

            res = parse_params(params_src)
            if res is None:
                Path(output_path).write_text(f"compiled: {source}")
                return -1
            _, _, c_params, param_info = res
            param_list = ", ".join(c_params)
            ret_resolved = resolve_type(ret_type) if ret_type else None
            if ret_type and ret_resolved is None:
                Path(output_path).write_text(f"compiled: {source}")
                return -1
            func_sigs[name] = {"params": param_info, "ret": ret_resolved or "void", "c_name": name}

            variables = {}
            for p in param_info:
                v_type = p["type"]
                c_t = p.get("c_type") or c_type_of(v_type)
                variables[p["name"]] = {
                    "type": v_type,
                    "c_type": c_t,
                    "mutable": False,
                    "bound": p.get("bound"),
                }

            has_nested = bool(header_pattern.search(body_str))

            init_lines = build_env_init(name, param_info) if has_nested and param_info else []

            ret_holder = {"type": ret_resolved}
            lines = compile_block(body_str, 1, variables, func_sigs, {}, ret_holder, name, has_nested)
            if lines is None:
                Path(output_path).write_text(f"compiled: {source}")
                return -1

            ret_kind = ret_holder.get("type") or "void"
            func_sigs[name]["ret"] = ret_kind
            if ret_kind == "void":
                c_ret = "void"
            else:
                c_ret = c_type_of(ret_kind)
                if not c_ret:
                    Path(output_path).write_text(f"compiled: {source}")
                    return -1

            body_text = "\n".join(init_lines + lines)
            if body_text:
                body_text += "\n"
            if name in func_structs:
                fields = env_struct_fields.get(name, [])
                if fields:
                    joined = "\n    ".join(f"{ftype} {fname};" for fname, ftype in fields)
                    structs.append(f"struct {name}_t {{\n    {joined}\n}};\n")
                else:
                    structs.append(f"struct {name}_t {{\n}};\n")
            funcs.append(f"{c_ret} {name}({param_list}) {{\n{body_text}}}\n")
            return new_pos

        pos = 0
        while pos < len(source):
            ws = re.match(r"\s*", source[pos:])
            pos += ws.end()
            if pos >= len(source):
                break

            import_match = import_pattern.match(source, pos)
            if import_match:
                name = import_match.group(1).lower()
                includes.append(f"#include <{name}.h>\n")
                pos = import_match.end()
                continue

            type_match = type_pattern.match(source, pos)
            if type_match:
                alias_name = type_match.group(1)
                base = type_match.group(2)
                resolved = resolve_type(base)
                if resolved not in self.NUMERIC_TYPE_MAP and resolved != "bool":
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                type_aliases[alias_name.lower()] = resolved
                pos = type_match.end()
                continue

            enum_match = enum_pattern.match(source, pos)
            if enum_match:
                name = enum_match.group(1)
                values_src = enum_match.group(2).strip()
                values = []
                if values_src:
                    values = [v.strip() for v in values_src.split(',') if v.strip()]
                    for val in values:
                        if not re.fullmatch(r"\w+", val):
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                enums.append(f"enum {name} {{ {', '.join(values)} }};\n")
                enum_names[name.lower()] = name
                pos = enum_match.end()
                continue

            struct_enum_match = struct_enum_pattern.match(source, pos)
            if struct_enum_match:
                name = struct_enum_match.group(1)
                values_src = struct_enum_match.group(2).strip()

                def split_variants(text: str):
                    parts = []
                    depth = 0
                    start = 0
                    for i, ch in enumerate(text):
                        if ch == '(': depth += 1
                        elif ch == ')': depth -= 1
                        elif ch == ',' and depth == 0:
                            parts.append(text[start:i].strip())
                            start = i + 1
                    tail = text[start:].strip()
                    if tail:
                        parts.append(tail)
                    return parts

                values = []
                union_lines = []
                if values_src:
                    raw_values = split_variants(values_src)
                    for val in raw_values:
                        m = re.fullmatch(r"(\w+)(?:\((.*?)\))?", val, re.DOTALL)
                        if not m:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        vname, params_src = m.groups()
                        if not re.fullmatch(r"\w+", vname):
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        values.append(vname)
                        if params_src:
                            res = parse_params(params_src, allow_bounds=False)
                            if res is None:
                                Path(output_path).write_text(f"compiled: {source}")
                                return
                            v_fields, c_fields, _, _ = res
                            struct_names[vname.lower()] = vname
                            struct_fields[vname] = v_fields
                            if c_fields:
                                joined = "\n    ".join(c_fields)
                                structs.append(f"struct {vname} {{\n    {joined}\n}};\n")
                            else:
                                structs.append(f"struct {vname} {{\n}};\n")
                        union_lines.append(f"struct {vname} {vname};")

                struct_enum_variants[name] = values

                enums.append(f"enum {name}Tag {{ {', '.join(values)} }};\n")
                union_text = "\n        ".join(union_lines)
                structs.append(
                    f"struct {name} {{\n"
                    f"    enum {name}Tag tag;\n"
                    f"    union {{\n        {union_text}\n    }} data;\n"
                    f"}};\n"
                )
                struct_names[name.lower()] = name
                struct_fields[name] = []
                pos = struct_enum_match.end()
                continue

            extern_match = extern_fn_pattern.match(source, pos)
            if extern_match:
                name = extern_match.group(1)
                params_src = extern_match.group(2).strip()
                ret_type = extern_match.group(3)
                res = parse_params(params_src)
                if res is None:
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                _, _, _, param_info = res
                ret_resolved = resolve_type(ret_type) if ret_type else None
                if ret_type and ret_resolved is None:
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                func_sigs[name] = {"params": param_info, "ret": ret_resolved or "void", "c_name": name}
                extern_found = True
                pos = extern_match.end()
                continue

            generic_match = generic_struct_pattern.match(source, pos)
            if generic_match:
                name = generic_match.group(1)
                param = generic_match.group(2)
                fields_src = generic_match.group(3).strip()
                generic_structs[name] = {"param": param, "fields": []}
                if fields_src:
                    fields = [f.strip() for f in fields_src.split(';') if f.strip()]
                    for field in fields:
                        field_match = field_pattern.fullmatch(field)
                        if not field_match:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        fname, ftype = field_match.groups()
                        if ftype != param:
                            base = resolve_type(ftype, {param.lower(): param.lower()})
                            if base is None:
                                Path(output_path).write_text(f"compiled: {source}")
                                return
                        generic_structs[name]["fields"].append((fname, ftype))
                pos = generic_match.end()
                continue

            res_callable = process_callable(pos)
            if res_callable == -1:
                return
            if res_callable is not None:
                pos = res_callable
                continue

            struct_match = struct_pattern.match(source, pos)
            if struct_match:
                name = struct_match.group(1)
                fields_src = struct_match.group(2).strip()
                struct_names[name.lower()] = name
                c_fields = []
                struct_fields[name] = []
                if fields_src:
                    fields = [f.strip() for f in fields_src.split(';') if f.strip()]
                    for field in fields:
                        field_match = field_pattern.fullmatch(field)
                        if not field_match:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        fname, ftype_raw = field_match.groups()
                        ftype = ftype_raw.strip()
                        func_match = func_type_pattern.fullmatch(ftype)
                        if func_match:
                            params_src = func_match.group(1).strip()
                            ret = func_match.group(2)
                            ret_base = resolve_type(ret) if ret.lower() != "void" else "void"
                            if ret_base is None:
                                Path(output_path).write_text(f"compiled: {source}")
                                return
                            c_ret = c_type_of(ret_base) if ret_base != "void" else "void"
                            if not c_ret:
                                Path(output_path).write_text(f"compiled: {source}")
                                return
                            param_bases = []
                            c_params = []
                            if params_src:
                                for p in [pt.strip() for pt in params_src.split(',') if pt.strip()]:
                                    base = resolve_type(p)
                                    if base is None:
                                        Path(output_path).write_text(f"compiled: {source}")
                                        return
                                    c_t = c_type_of(base)
                                    if not c_t:
                                        Path(output_path).write_text(f"compiled: {source}")
                                        return
                                    param_bases.append(base)
                                    c_params.append(c_t)
                            c_param_list = ", ".join(c_params)
                            struct_fields[name].append((fname, f"fn({', '.join(param_bases)})->{ret_base}"))
                            c_fields.append(f"{c_ret} (*{fname})({c_param_list});")
                            continue
                        base = resolve_type(ftype)
                        c_type = c_type_of(base)
                        if not c_type:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        struct_fields[name].append((fname, base))
                        c_fields.append(f"{c_type} {fname};")
                if c_fields:
                    joined = "\n    ".join(c_fields)
                    structs.append(f"struct {name} {{\n    {joined}\n}};\n")
                else:
                    structs.append(f"struct {name} {{\n}};\n")
                pos = struct_match.end()
                continue

            let_match = let_pattern.match(source, pos)
            if let_match:
                if let_match.group(1) is not None:
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                var_name = let_match.group(2)
                var_type = let_match.group(3)
                value = let_match.group(4)
                variant_init = None
                if value:
                    variant_init = variant_init_pattern.fullmatch(value.strip())
                if (
                    var_type
                    and value
                    and var_type.strip().endswith(")")
                    and value.strip().startswith(">")
                ):
                    var_type = f"{var_type.strip()} => {value.strip()[1:].strip()}"
                    value = None
                if variant_init:
                    base_name, vname, params_src = variant_init.groups()
                    params = [p.strip() for p in params_src.split(',') if p.strip()] if params_src else []
                    if var_type:
                        if '.' in var_type.strip():
                            vt_base, vt_var = var_type.strip().split('.', 1)
                            if vt_var != vname:
                                Path(output_path).write_text(f"compiled: {source}")
                                return
                        else:
                            vt_base = var_type.strip()
                        base = resolve_type(vt_base)
                        if base is None or resolve_type(base_name) != base:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                    else:
                        base = resolve_type(base_name)
                        if base is None:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                    if base not in struct_enum_variants or vname not in struct_enum_variants[base]:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    fields = struct_fields.get(vname, [])
                    if len(params) != len(fields):
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    globals.append(f"struct {base} {var_name};\n")
                    globals.append(f"{var_name}.tag = {vname};\n")
                    for val_item, (fname, ftype) in zip(params, fields):
                        if ftype == "bool":
                            if val_item.lower() in {"true", "false"}:
                                c_val = "1" if val_item.lower() == "true" else "0"
                            elif val_item in global_vars and global_vars[val_item]["type"] == "bool":
                                c_val = val_item
                            else:
                                expr_info = analyze_expr(val_item, global_vars, func_sigs)
                                if not expr_info or expr_info["type"] != "bool":
                                    Path(output_path).write_text(f"compiled: {source}")
                                    return
                                c_val = expr_info["c_expr"]
                        else:
                            if re.fullmatch(r"[0-9]+", val_item) or parse_arithmetic(val_item) is not None:
                                c_val = val_item
                            elif val_item in global_vars and (
                                global_vars[val_item]["type"] in self.NUMERIC_TYPE_MAP or global_vars[val_item]["type"] == "i32"
                            ):
                                c_val = val_item
                            else:
                                expr_info = analyze_expr(val_item, global_vars, func_sigs)
                                if not expr_info or expr_info["type"] != "i32":
                                    Path(output_path).write_text(f"compiled: {source}")
                                    return
                                c_val = expr_info["c_expr"]
                        globals.append(f"{var_name}.data.{vname}.{fname} = {c_val};\n")
                    global_vars[var_name] = {"type": base, "c_type": f"struct {base}"}
                    pos = let_match.end()
                    continue
                inline_cls = parse_inline_class(value.strip()) if value else None
                if inline_cls:
                    cls_name, params_src, body_str, args_src = inline_cls
                    if var_type and var_type.strip() != cls_name:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    if cls_name in struct_fields:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    struct_names[cls_name.lower()] = cls_name
                    struct_fields[cls_name] = []
                    res = parse_params(params_src, allow_bounds=False)
                    if res is None:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    struct_fields[cls_name], c_fields, c_params, param_info = res
                    if c_fields:
                        joined = "\n    ".join(c_fields)
                        structs.append(f"struct {cls_name} {{\n    {joined}\n}};\n")
                    else:
                        structs.append(f"struct {cls_name} {{\n}};\n")
                    func_lines = [f"struct {cls_name} {cls_name}({', '.join(c_params)}) {{"]
                    func_lines.append(f"    struct {cls_name} this;")
                    for fname, _ in struct_fields[cls_name]:
                        func_lines.append(f"    this.{fname} = {fname};")
                    func_lines.append("    return this;")
                    func_lines.append("}")
                    funcs.append("\n".join(func_lines) + "\n")
                    func_sigs[cls_name] = {"params": param_info, "ret": cls_name, "c_name": cls_name}
                    arg_items = [a.strip() for a in args_src.split(',') if a.strip()] if args_src else []
                    if len(arg_items) != len(param_info):
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    c_args = []
                    for arg, param in zip(arg_items, param_info):
                        expr_info = analyze_expr(arg, global_vars, func_sigs)
                        if not expr_info or expr_info["type"] != param["type"]:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        c_args.append(expr_info["c_expr"])
                    arg_list = ", ".join(c_args)
                    globals.append(f"struct {cls_name} {var_name} = {cls_name}({arg_list});\n")
                    global_vars[var_name] = {"type": cls_name, "c_type": f"struct {cls_name}"}
                    pos = let_match.end()
                    continue
                if value is None:
                    if not var_type:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    func_match = re.fullmatch(r"\(\s*(.*?)\s*\)\s*=>\s*(\w+)", var_type.strip())
                    if func_match:
                        params_src = func_match.group(1).strip()
                        ret = func_match.group(2)
                        ret_base = resolve_type(ret) if ret.lower() != "void" else "void"
                        if ret_base is None:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        if ret_base == "bool":
                            c_ret = "int"
                        elif ret_base in self.NUMERIC_TYPE_MAP:
                            c_ret = self.NUMERIC_TYPE_MAP[ret_base]
                        elif ret_base in struct_names.values():
                            c_ret = f"struct {ret_base}"
                        elif ret_base == "void":
                            c_ret = "void"
                        else:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        c_params = []
                        param_bases = []
                        if params_src:
                            for p in [pt.strip() for pt in params_src.split(',') if pt.strip()]:
                                base = resolve_type(p)
                                if base is None:
                                    Path(output_path).write_text(f"compiled: {source}")
                                    return
                                if base == "bool":
                                    c_t = "int"
                                elif base in self.NUMERIC_TYPE_MAP:
                                    c_t = self.NUMERIC_TYPE_MAP[base]
                                elif base in struct_names.values():
                                    c_t = f"struct {base}"
                                else:
                                    Path(output_path).write_text(f"compiled: {source}")
                                    return
                                c_params.append(c_t)
                                param_bases.append(base)
                        c_param_list = ", ".join(c_params)
                        globals.append(f"{c_ret} (*{var_name})({c_param_list});\n")
                        global_vars[var_name] = {"type": f"fn({', '.join(param_bases)})->{ret_base}", "c_type": f"{c_ret} (*)({c_param_list})"}
                        pos = let_match.end()
                        continue
                    if var_type.strip().startswith("*"):
                        inner_base = resolve_type(var_type.strip()[1:])
                        if inner_base is None:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        c_inner = c_type_of(inner_base)
                        if not c_inner:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        globals.append(f"{c_inner}* {var_name};\n")
                        global_vars[var_name] = {"type": f"*{inner_base}", "c_type": f"{c_inner}*"}
                        pos = let_match.end()
                        continue
                    base = resolve_type(var_type.strip())
                    if base not in struct_names.values():
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    globals.append(f"struct {base} {var_name};\n")
                    global_vars[var_name] = {"type": base, "c_type": f"struct {base}"}
                    pos = let_match.end()
                    continue
                struct_init = re.fullmatch(r"(\w+)\s*{\s*(.*?)\s*}", value.strip(), re.DOTALL)
                if not struct_init:
                    if var_type and var_type.strip().startswith("*"):
                        inner_base = resolve_type(var_type.strip()[1:])
                        if inner_base is None:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        c_inner = c_type_of(inner_base)
                        if not c_inner:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        if value.strip().startswith("&") and value.strip()[1:] in global_vars:
                            ref = value.strip()[1:]
                            if global_vars[ref]["type"] != inner_base:
                                Path(output_path).write_text(f"compiled: {source}")
                                return
                            c_value = f"&{ref}"
                        else:
                            expr_info = analyze_expr(value.strip(), global_vars, func_sigs)
                            if not expr_info or expr_info["type"] != f"*{inner_base}":
                                Path(output_path).write_text(f"compiled: {source}")
                                return
                            c_value = expr_info["c_expr"]
                        globals.append(f"{c_inner}* {var_name} = {c_value};\n")
                        global_vars[var_name] = {"type": f"*{inner_base}", "c_type": f"{c_inner}*"}
                        pos = let_match.end()
                        continue
                    expr_info = analyze_expr(value.strip(), global_vars, func_sigs)
                    if not expr_info:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    if var_type:
                        base = resolve_type(var_type.strip())
                        if expr_info["type"] != base:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                    else:
                        base = expr_info["type"]
                    c_type = c_type_of(base)
                    if not c_type:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    globals.append(f"{c_type} {var_name} = {expr_info['c_expr']};\n")
                    global_vars[var_name] = {"type": base, "c_type": c_type}
                    pos = let_match.end()
                    continue
                init_name = struct_init.group(1)
                vals = [v.strip() for v in struct_init.group(2).split(',') if v.strip()]
                if var_type:
                    base = resolve_type(var_type.strip())
                    vm = re.fullmatch(r"(\w+)\s*<\s*(\w+)\s*>", var_type.strip())
                    expected = vm.group(1) if vm else var_type.strip()
                    if init_name != expected:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    base_init = base
                else:
                    base_init = resolve_type(init_name)
                    if base_init not in struct_fields:
                        Path(output_path).write_text(f"compiled: {source}")
                        return
                    base = base_init
                fields = struct_fields[base_init]
                if len(vals) != len(fields):
                    Path(output_path).write_text(f"compiled: {source}")
                    return
                globals.append(f"struct {base} {var_name};\n")
                global_vars[var_name] = {"type": base, "c_type": f"struct {base}"}
                for val, (fname, ftype) in zip(vals, fields):
                    if ftype == "bool":
                        if val.lower() in {"true", "false"}:
                            c_val = "1" if val.lower() == "true" else "0"
                        else:
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                    else:
                        if not re.fullmatch(r"[0-9]+", val):
                            Path(output_path).write_text(f"compiled: {source}")
                            return
                        c_val = val
                    globals.append(f"{var_name}.{fname} = {c_val};\n")
                pos = let_match.end()
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

            res = parse_params(params_src)
            if res is None:
                Path(output_path).write_text(f"compiled: {source}")
                return
            _, _, c_params, param_info = res
            param_list = ", ".join(c_params)
            ret_resolved = resolve_type(ret_type) if ret_type else None
            if ret_type and ret_resolved is None:
                Path(output_path).write_text(f"compiled: {source}")
                return
            func_sigs[name] = {"params": param_info, "ret": ret_resolved or "void", "c_name": name}

            variables = {}
            for p in param_info:
                v_type = p["type"]
                c_t = p.get("c_type") or c_type_of(v_type)
                variables[p["name"]] = {
                    "type": v_type,
                    "c_type": c_t,
                    "mutable": False,
                    "bound": p.get("bound"),
                }

            has_nested = bool(header_pattern.search(body_str))

            init_lines = build_env_init(name, param_info) if has_nested and param_info else []

            ret_holder = {"type": ret_resolved}
            lines = compile_block(body_str, 1, variables, func_sigs, {}, ret_holder, name, has_nested)
            if lines is None:
                Path(output_path).write_text(f"compiled: {source}")
                return

            ret_kind = ret_holder.get("type") or "void"
            func_sigs[name]["ret"] = ret_kind
            if ret_kind == "void":
                c_ret = "void"
            else:
                c_ret = c_type_of(ret_kind)
                if not c_ret:
                    Path(output_path).write_text(f"compiled: {source}")
                    return

            body_text = "\n".join(init_lines + lines)
            if body_text:
                body_text += "\n"
            if name in func_structs:
                fields = env_struct_fields.get(name, [])
                if fields:
                    joined = "\n    ".join(f"{ftype} {fname};" for fname, ftype in fields)
                    structs.append(f"struct {name}_t {{\n    {joined}\n}};\n")
                else:
                    structs.append(f"struct {name}_t {{\n}};\n")
            funcs.append(f"{c_ret} {name}({param_list}) {{\n{body_text}}}\n")

        output = "".join(includes) + "".join(structs) + "".join(enums) + "".join(globals) + "".join(funcs)
        if output or generic_classes or generic_structs or generic_funcs or type_aliases or extern_found:
            Path(output_path).write_text(output)
        else:
            Path(output_path).write_text(f"compiled: {source}")

if __name__ == "__main__":
    compiler = Compiler()
    repo_root = Path(__file__).resolve().parents[2]
    input_file = repo_root / "working" / "main.mg"
    output_file = repo_root / "working" / "main.c"
    compiler.compile(input_file, output_file)