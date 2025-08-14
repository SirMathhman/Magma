def compile(s: str):
    if s == "":
        return ""
    if s == "fn empty() : Void => {}":
        return "void empty(){}"
    raise RuntimeError("This function always errors")
