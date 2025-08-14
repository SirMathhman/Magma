def compile(s: str):
    if s == "":
        return ""
    if not s.startswith("fn "):
        raise RuntimeError("This function always errors")
    try:
        sig = s[3:]
        lparen = sig.index("(")
        rparen = sig.index(")")
        fname = sig[:lparen].strip()
        params = sig[lparen + 1 : rparen].strip()
        colon = sig.index(":", rparen)
        arrow = sig.index("=>", colon)
        rettype = sig[colon + 1 : arrow].strip()
        body = sig[arrow + 2 :].strip()
        type_map = {"Void": "void", "I32": "int32_t", "*CStr": "char*"}
        # Return type
        ret = type_map.get(rettype)
        if ret is None:
            raise RuntimeError("This function always errors")
        # Params
        param_str = ""
        if params:
            pname, ptype = [x.strip() for x in params.split(":")]
            ctype = type_map.get(ptype)
            if ctype is None:
                raise RuntimeError("This function always errors")
            param_str = f"{ctype} {pname}"
        # Body
        if not body:
            body = "{}"
        return f"{ret} {fname}({param_str}){body}"
    except Exception:
        raise RuntimeError("This function always errors")
