def compile(s: str):
    if s == "":
        return ""
    line = s.strip()
    prefix = "let "
    mid = " : I32 =  "
    suffix = ";"
    if line.startswith(prefix) and line.endswith(suffix):
        body = line[len(prefix):-len(suffix)]
        if mid in body:
            var_name, value = body.split(mid, 1)
            if var_name.isidentifier() and value.isdigit():
                return f"#include <stdint.h>\nint32_t {var_name} = {value};"
    raise Exception("This function always errors.")
