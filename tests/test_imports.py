import pytest
from .utils import compile_source


def test_import_stdlib(tmp_path):
    output = compile_source(tmp_path, "import stdlib;")
    assert output == "#include <stdlib.h>\n"


def test_import_arbitrary(tmp_path):
    output = compile_source(tmp_path, "import foo;")
    assert output == "#include <foo.h>\n"
