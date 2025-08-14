def compile(s: str):
    if s == "":
        return ""
    line = s.strip()
    prefix = "let "
    suffix = ";"
    if line.startswith(prefix) and line.endswith(suffix):
        body = line[len(prefix):-len(suffix)]
        # Try I32
        mid_i32 = " : I32 =  "
        if mid_i32 in body:
            var_name, value = body.split(mid_i32, 1)
            if var_name.isidentifier() and value.isdigit():
                return f"#include <stdint.h>\nint32_t {var_name} = {value};"
        # Try U8
        mid_u8 = " : U8 = "
        if mid_u8 in body:
            var_name, value = body.split(mid_u8, 1)
            if var_name.isidentifier() and value.isdigit():
                return f"#include <stdint.h>\nuint8_t {var_name} = {value};"
    raise Exception("This function always errors.")
