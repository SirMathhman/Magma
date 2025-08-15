def always_throws(s: str) -> str:
    """Accept a string and produce a string if the input is empty.

    If `s` is an empty string, return a non-empty string result.
    Otherwise raise a RuntimeError.
    """
    if not isinstance(s, str):
        raise TypeError("Input must be a string")
    if s == "":
        return "empty"
    raise RuntimeError("Input must be empty")
