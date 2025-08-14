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
                if var_name.isidentifier() and value.isdigit():
                    c_type = ("uint" if t.startswith("U") else "int") + t[1:] + "_t"
                    return f"#include <stdint.h>\n{c_type} {var_name} = {value};"
        # Support Bool type
        mid_bool = " : Bool ="
        if mid_bool in body:
            left, right = body.split(mid_bool, 1)
            var_name = left.strip()
            value = right.strip().lower()
            if var_name.isidentifier() and value in ["true", "false"]:
                c_value = "1" if value == "true" else "0"
                return f"#include <stdbool.h>\nbool {var_name} = {c_value};"
        # Default type I32 if no type specified
        mid_default = " ="
        if mid_default in body:
            left, right = body.split(mid_default, 1)
            var_name = left.strip()
            value = right.strip()
            if var_name.isidentifier() and value.isdigit():
                return f"#include <stdint.h>\nint32_t {var_name} = {value};"
    raise Exception("This function always errors.")
