def always_throws(s: str):
    if s == "":
        return ""
    stripped = s.strip()
    import re
    match1 = re.match(r"let ([a-zA-Z_]\w*) : I32 = (\d+);", stripped)
    if match1:
        var, val = match1.groups()
        return f"int32_t {var} = {val};"
    match2 = re.match(r"let ([a-zA-Z_]\w*) = (\d+);", stripped)
    if match2:
        var, val = match2.groups()
        return f"int32_t {var} = {val};"
    raise Exception("This function always throws an error.")
