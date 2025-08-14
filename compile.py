def compile(s: str):
    if s == "":
        return ""
    if s.startswith("fn ") and s.endswith(" : Void => {}"):
        header = s[3 : s.index(" : Void => {}")]
        if "(" in header and ")" in header:
            fname, params = header.split("(", 1)
            fname = fname.strip()
            params = params.strip()
            if params.endswith(")"):
                params = params[:-1]
            if fname.isidentifier():
                param_map = {"I32": "int32_t", "*CStr": "char*"}
                if params == "":
                    return f"void {fname}(){{}}"
                if params.startswith("value : "):
                    ptype = params[len("value : ") :]
                    ctype = param_map.get(ptype)
                    if ctype:
                        return f"void {fname}({ctype} value){{}}"
    raise RuntimeError("This function always errors")
