def always_throws(s: str):
    if s == "":
        return ""
    stripped = s.strip()
    import re
    match = re.match(r"let ([a-zA-Z_]\w*) : I32 = (\d+);", stripped)
    if match:
        var, val = match.groups()
        return f"int32_t {var} = {val};"
    raise Exception("This function always throws an error.")
