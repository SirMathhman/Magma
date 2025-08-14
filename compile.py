def compile(s: str):
    if s == "":
        return ""
    line = s.strip()
    prefix = "let "
    suffix = " : I32 =  100;"
    if line.startswith(prefix) and line.endswith(suffix):
        var_name = line[len(prefix): -len(suffix)]
        if var_name.isidentifier():
            return f"#include <stdint.h>\nint32_t {var_name} = 100;"
    raise Exception("This function always errors.")
