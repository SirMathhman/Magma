def compile(s: str):
    if s == "":
        return ""
    if s.startswith("fn ") and s.endswith("() : Void => {}"):
        name = s[3 : s.index("() : Void => {}")].strip()
        if name.isidentifier():
            return f"void {name}(){{}}"
    raise RuntimeError("This function always errors")
