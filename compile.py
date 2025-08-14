def compile(s: str):
    if s == "":
        return ""
    line = s.strip()
    prefix = "let "
    suffix = ";"
    if line.startswith(prefix) and line.endswith(suffix):
        body = line[len(prefix) : -len(suffix)]
        for t in ["U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"]:
            mid = f" : {t} ="
            if mid in body:
                left, right = body.split(mid, 1)
                var_name = left.strip()
                value = right.strip()
                import re

                match = re.match(r"([0-9]+)([IU][0-9]+)", value)
                if match and var_name.isidentifier():
                    num, type_suffix = match.groups()
                    # Only allow if type_suffix matches annotation
                    if type_suffix == t:
                        if type_suffix.startswith("I"):
                            c_type = f"int{type_suffix[1:]}_t"
                        else:
                            c_type = f"uint{type_suffix[1:]}_t"
                        return f"#include <stdint.h>\n{c_type} {var_name} = {num};"
                    else:
                        raise Exception(
                            "Type annotation and literal type suffix do not match."
                        )
                # fallback: if value is just a digit, use annotation type
                if var_name.isidentifier() and value.isdigit():
                    c_type = ("uint" if t.startswith("U") else "int") + t[1:] + "_t"
                    return f"#include <stdint.h>\n{c_type} {var_name} = {value};"
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
                    return f"float {var_name} = {num};"
                elif type_suffix == "F64":
                    return f"double {var_name} = {num};"
            # Default type I32 if no type specified and value is integer
            if var_name.isidentifier() and value.isdigit():
                return f"#include <stdint.h>\nint32_t {var_name} = {value};"
    raise Exception("This function always errors.")
