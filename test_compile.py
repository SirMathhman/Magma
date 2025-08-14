import compile


def test_always_empty():
    assert compile.always_empty("hello") == ""
    assert compile.always_empty("") == ""
    assert compile.always_empty("anything at all") == ""
