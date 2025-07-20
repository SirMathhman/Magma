"""Numeric helpers used by the Magma compiler."""

from __future__ import annotations

import re

NUMERIC_TYPE_MAP = {
    "u8": "unsigned char",
    "u16": "unsigned short",
    "u32": "unsigned int",
    "u64": "unsigned long long",
    "usize": "unsigned long",
    "i8": "signed char",
    "i16": "short",
    "i32": "int",
    "i64": "long long",
}


def parse_arithmetic(expr: str):
    """Return the value of a simple arithmetic expression or ``None``."""
    token = expr.replace(" ", "")
    if not token or not re.fullmatch(r"[0-9+\-*/()]+", token):
        return None
    try:
        return eval(token, {"__builtins__": {}})
    except Exception:
        return None


def parse_numeric_condition(cond: str):
    """Parse ``x < 4`` style comparisons and return components."""
    m = re.fullmatch(r"(\w+)\s*(==|<=|>=|<|>)\s*([0-9]+)", cond)
    if m:
        return m.group(1), m.group(2), int(m.group(3))
    m = re.fullmatch(r"([0-9]+)\s*(==|<=|>=|<|>)\s*(\w+)", cond)
    if m:
        inv = {"<": ">", ">": "<", "<=": ">=", ">=": "<=", "==": "=="}
        return m.group(3), inv[m.group(2)], int(m.group(1))
    return None


def range_from_op(op: str, val: int):
    if op == ">":
        return (val, False, None, True)
    if op == ">=":
        return (val, True, None, True)
    if op == "<":
        return (None, True, val, False)
    if op == "<=":
        return (None, True, val, True)
    if op == "==":
        return (val, True, val, True)
    return (None, True, None, True)


def intersect_range(a, b):
    low1, inc1, up1, inc1u = a
    low2, inc2, up2, inc2u = b
    low = low1
    inc_low = inc1
    if low2 is not None:
        if low is None or low2 > low or (low2 == low and not inc_low):
            low = low2
            inc_low = inc2
        elif low2 == low:
            inc_low = inc_low and inc2
    upper = up1
    inc_up = inc1u
    if up2 is not None:
        if upper is None or up2 < upper or (up2 == upper and not inc_up):
            upper = up2
            inc_up = inc2u
        elif up2 == upper:
            inc_up = inc_up and inc2u
    if low is not None and upper is not None:
        if low > upper:
            return None
        if low == upper and (not inc_low or not inc_up):
            return None
    return (low, inc_low, upper, inc_up)


def bound_to_range(b):
    if not b:
        return (None, True, None, True)
    op, val = b
    return range_from_op(op, val)


def is_subset(inner, outer):
    inter = intersect_range(inner, outer)
    return inter is not None and inter == inner
