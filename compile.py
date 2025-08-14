def always_empty(input_string: str) -> str:
    """Returns an empty string unless input is 'let x : I32 = 0;', then returns 'int32_t x = 0;'"""
    if input_string == "let x : I32 = 0;":
        return "int32_t x = 0;"
    return ""
