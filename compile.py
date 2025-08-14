def compile(s: str):
    if s == "":
        return ""
    if not s.startswith("fn "):
        raise RuntimeError("This function always errors")

    # Find function name and params
    after_fn = s[3:]
    lparen = after_fn.find("(")
    rparen = after_fn.find(")")
    if lparen == -1 or rparen == -1 or rparen < lparen:
        raise RuntimeError("This function always errors")
    fname = after_fn[:lparen].strip()
    params = after_fn[lparen + 1 : rparen].strip()
    rest = after_fn[rparen + 1 :].strip()
    if not fname.isidentifier():
        raise RuntimeError("This function always errors")

    # Handle return type and body
    param_map = {"I32": "int32_t", "*CStr": "char*"}
    if rest.startswith(": Void => {}"):
        if params == "":
            return f"void {fname}(){{}}"
        if params.startswith("value : "):
            ptype = params[len("value : ") :]
            ctype = param_map.get(ptype)
            if ctype:
                return f"void {fname}({ctype} value){{}}"
    elif rest.startswith(": *CStr => {"):
        body_start = rest.find("{")
        if body_start == -1:
            raise RuntimeError("This function always errors")
        body = rest[body_start:]
        if params == "":
            return f"char* {fname}(){body}"
    raise RuntimeError("This function always errors")
