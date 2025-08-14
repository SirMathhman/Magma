import pytest
from compile import compile

def test_compile_empty():
    assert compile("") == ""

def test_compile_error():
    with pytest.raises(Exception, match="This function always errors."):
        compile("abc")
