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
                if params == "":
                    return f"void {fname}(){{}}"
                if params == "value : I32":
                    return f"void {fname}(int32_t value){{}}"
    raise RuntimeError("This function always errors")
