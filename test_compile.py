import pytest
from compile import always_error


def test_always_error():
    with pytest.raises(RuntimeError, match="always errors"):
        always_error()
