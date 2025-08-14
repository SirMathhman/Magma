def always_empty(input_string: str) -> str:
    """Returns an empty string unless input is 'let x : I32 = 0;', then returns 'int32_t x = 0;'"""
    prefix = "let "
    if input_string.startswith(prefix):
        if input_string.endswith(" = 0;"):
            middle = input_string[len(prefix) : -len(" = 0;")]
            parts = middle.split(" : ")
            if len(parts) == 2:
                var_name, type_name = parts
                valid_types = {
                    "U8": "uint8_t",
                    "U16": "uint16_t",
                    "U32": "uint32_t",
                    "U64": "uint64_t",
                    "I8": "int8_t",
                    "I16": "int16_t",
                    "I32": "int32_t",
                    "I64": "int64_t",
                }
                if (
                    var_name
                    and all(c.isalnum() or c == "_" for c in var_name)
                    and type_name in valid_types
                ):
                    return f"{valid_types[type_name]} {var_name} = 0;"
        elif input_string.endswith(" = true;") or input_string.endswith(" = false;"):
            value = "true" if input_string.endswith(" = true;") else "false"
            middle = input_string[len(prefix) : -len(f" = {value};")]
            parts = middle.split(" : ")
            if len(parts) == 2:
                var_name, type_name = parts
                if (
                    var_name
                    and all(c.isalnum() or c == "_" for c in var_name)
                    and type_name == "Bool"
                ):
                    return f"bool {var_name} = {value};"
    return ""
