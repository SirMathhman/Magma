def always_throws(s: str):
    if s == "":
        return ""
    raise Exception("This function always throws an error.")
