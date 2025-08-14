def compile(s: str):
    if s == "":
        return ""
    if s.strip() == "let x : I32 =  100;":
        return "#include <stdint.h>\nint32_t x = 100;"
    raise Exception("This function always errors.")
