import pytest
from compile import compile


def test_compile_empty():
    assert compile("") == ""


def test_compile_fn_empty():
    assert compile("fn empty() : Void => {}") == "void empty(){}"


def test_compile_accept_i32():
    assert (
        compile("fn accept(value : I32) : Void => {}") == "void accept(int32_t value){}"
    )


def test_compile_error():
    with pytest.raises(RuntimeError, match="always errors"):
        compile("not empty")
