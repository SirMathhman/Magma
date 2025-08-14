def compile(s: str):
    if s == "":
        return ""
    line = s.strip()
    prefix = "let "
    suffix = ";"
    if line.startswith(prefix) and line.endswith(suffix):
        body = line[len(prefix) : -len(suffix)]
        for t in [
            "U8",
            "U16",
            "U32",
            "U64",
            "I8",
            "I16",
            "I32",
            "I64",
            "F32",
            "F64",
            "Bool",
        ]:
            mid = f" : {t} ="
            if mid in body:
                left, right = body.split(mid, 1)
                var_name = left.strip()
                value = right.strip()
                import re

                # Infer type of right-hand side
                inferred_type = None
                # Integer with suffix
                m = re.match(r"([0-9]+)([IU][0-9]+)", value)
                if m:
                    num, type_suffix = m.groups()
                    inferred_type = type_suffix
                # Float with suffix
                m = re.match(r"([0-9]+\.[0-9]+)(F32|F64)", value)
                if m:
                    num, type_suffix = m.groups()
                    inferred_type = type_suffix
                # Bool
                if value.lower() in ["true", "false"]:
                    inferred_type = "Bool"
                # Plain integer
                if value.isdigit():
                    inferred_type = (
                        t
                        if t in ["U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"]
                        else None
                    )
                # Plain float
                try:
                    float_val = float(value)
                    if "." in value:
                        inferred_type = t if t in ["F32", "F64"] else "F32"
                except ValueError:
                    pass
                # Check type match
                if inferred_type != t:
                    raise Exception(
                        f"Type annotation '{t}' does not match inferred type '{inferred_type}'."
                    )
                # Generate output
                if t in ["I8", "I16", "I32", "I64"]:
                    c_type = f"int{t[1:]}_t"
                    return f"#include <stdint.h>\n{c_type} {var_name} = {value};"
                elif t in ["U8", "U16", "U32", "U64"]:
                    c_type = f"uint{t[1:]}_t"
                    return f"#include <stdint.h>\n{c_type} {var_name} = {value};"
                elif t == "F32":
                    return f"float {var_name} = {value};"
                elif t == "F64":
                    return f"double {var_name} = {value};"
                elif t == "Bool":
                    c_value = "1" if value.lower() == "true" else "0"
                    return f"#include <stdbool.h>\nbool {var_name} = {c_value};"
        # Support F32 and F64 types
        for t, c_type in [("F32", "float"), ("F64", "double")]:
            mid = f" : {t} ="
            if mid in body:
                left, right = body.split(mid, 1)
                var_name = left.strip()
                value = right.strip()
                # Accept valid float literals
                try:
                    float(value)
                except ValueError:
                    continue
                if var_name.isidentifier():
                    return f"{c_type} {var_name} = {value};"
        # Support Bool type
        mid_bool = " : Bool ="
        if mid_bool in body:
            left, right = body.split(mid_bool, 1)
            var_name = left.strip()
            value = right.strip().lower()
            if var_name.isidentifier() and value in ["true", "false"]:
                c_value = "1" if value == "true" else "0"
                return f"#include <stdbool.h>\nbool {var_name} = {c_value};"
        # Support literals with type suffix, e.g., 0I32, 1U8, 2F32
        mid_default = " ="
        if mid_default in body:
            left, right = body.split(mid_default, 1)
            var_name = left.strip()
            value = right.strip()
            import re

            match = re.match(r"([0-9]+(?:\.[0-9]+)?)([A-Za-z0-9]+)", value)
            if match and var_name.isidentifier():
                num, type_suffix = match.groups()
                # Integer types
                if type_suffix in ["I8", "I16", "I32", "I64"]:
                    c_type = f"int{type_suffix[1:]}_t"
                    return f"#include <stdint.h>\n{c_type} {var_name} = {num};"
                elif type_suffix in ["U8", "U16", "U32", "U64"]:
                    c_type = f"uint{type_suffix[1:]}_t"
                    return f"#include <stdint.h>\n{c_type} {var_name} = {num};"
                elif type_suffix == "F32":
                    try:
                        float(num)
                    except ValueError:
                        raise Exception("Invalid F32 literal.")
                    return f"float {var_name} = {num};"
                elif type_suffix == "F64":
                    try:
                        float(num)
                    except ValueError:
                        raise Exception("Invalid F64 literal.")
                    return f"double {var_name} = {num};"
            # Default type I32 if no type specified and value is integer
            if var_name.isidentifier() and value.isdigit():
                return f"#include <stdint.h>\nint32_t {var_name} = {value};"
            # Default type F32 if no type specified and value is float
            try:
                float_val = float(value)
                # Only treat as float if it contains a decimal point
                if var_name.isidentifier() and "." in value:
                    return f"float {var_name} = {value};"
            except ValueError:
                pass
    raise Exception("This function always errors.")
