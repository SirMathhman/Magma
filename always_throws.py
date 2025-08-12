def always_throws(s: str):
    if s == "":
        return ""
    stripped = s.strip()
    if stripped.startswith("let ") and stripped.endswith(";"):
        body = stripped[4:-1].strip()
        # Handle 'x : I32 = 200' and 'y : I32 = 100'
        if " : I32 = " in body:
            parts = body.split(" : I32 = ")
            if len(parts) == 2:
                var = parts[0].strip()
                val = parts[1].strip()
                if val.isdigit():
                    return f"int32_t {var} = {val};"
        # Handle 'x = 200'
        elif " = " in body:
            parts = body.split(" = ")
            if len(parts) == 2:
                var = parts[0].strip()
                val = parts[1].strip()
                if val.isdigit():
                    return f"int32_t {var} = {val};"
    raise Exception("This function always throws an error.")
