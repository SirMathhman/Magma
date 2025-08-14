import compile


def test_always_empty():
    assert compile.always_empty("hello") == ""
    assert compile.always_empty("") == ""
    assert compile.always_empty("anything at all") == ""


def test_let_x_I32():
    assert compile.always_empty("let x : I32 = 0;") == "int32_t x = 0;"


def test_let_y_I32():
    assert compile.always_empty("let y : I32 = 0;") == "int32_t y = 0;"


def test_let_x_I32_different_value():
    assert compile.always_empty("let x : I32 = 42;") == ""


def test_let_y_I32_negative_value():
    assert compile.always_empty("let y : I32 = -7;") == ""


def test_unsigned_types():
    assert compile.always_empty("let a : U8 = 0;") == "uint8_t a = 0;"
    assert compile.always_empty("let b : U16 = 0;") == "uint16_t b = 0;"
    assert compile.always_empty("let c : U32 = 0;") == "uint32_t c = 0;"
    assert compile.always_empty("let d : U64 = 0;") == "uint64_t d = 0;"


def test_signed_types():
    assert compile.always_empty("let e : I8 = 0;") == "int8_t e = 0;"
    assert compile.always_empty("let f : I16 = 0;") == "int16_t f = 0;"
    assert compile.always_empty("let g : I32 = 0;") == "int32_t g = 0;"
    assert compile.always_empty("let h : I64 = 0;") == "int64_t h = 0;"
