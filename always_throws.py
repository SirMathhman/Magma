def convert_let_to_c_type(s: str):
    if s == "":
        return ""
    stripped = s.strip()
    if stripped.startswith("let ") and stripped.endswith(";"):
        body = stripped[4:-1].strip()
        # Handle 'x : TYPE = VALUE' for U8-U64 and I8-I64
        for t in ["U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"]:
            type_str = f" : {t} = "
            if type_str in body:
                parts = body.split(type_str)
                if len(parts) == 2:
                    var = parts[0].strip()
                    val = parts[1].strip()
                    if val.isdigit():
                        ctype = ("uint" if t.startswith("U") else "int") + t[1:] + "_t"
                        return f"{ctype} {var} = {val};"
        # Handle 'x = 200' and 'x = 0U8' style
        if " = " in body:
            parts = body.split(" = ")
            if len(parts) == 2:
                var = parts[0].strip()
                val = parts[1].strip()
                # Check for value with type suffix (e.g., 0U8, 1I16)
                for t in ["U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"]:
                    if val.endswith(t):
                        num = val[:-len(t)]
                        if num.isdigit():
                            ctype = ("uint" if t.startswith("U") else "int") + t[1:] + "_t"
                            return f"{ctype} {var} = {num};"
                # Default int32_t if just a number
                if val.isdigit():
                    return f"int32_t {var} = {val};"
    raise Exception("This function always throws an error.")
