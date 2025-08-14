import compile


def test_always_empty():
    assert compile.always_empty("hello") == ""
    assert compile.always_empty("") == ""
    assert compile.always_empty("anything at all") == ""


def test_let_x_I32():
    assert compile.always_empty("let x : I32 = 0;") == "int32_t x = 0;"
