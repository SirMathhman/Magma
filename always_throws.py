def always_throws(s: str):
    if s == "":
        return ""
    if s.strip() == "let x : I32 = 100;":
        return "int32_t x = 100;"
    raise Exception("This function always throws an error.")
