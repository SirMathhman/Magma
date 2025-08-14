import pytest
from compile import compile

def test_compile():
    with pytest.raises(Exception, match="This function always errors."):
        compile()
