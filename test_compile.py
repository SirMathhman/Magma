import compile


def test_always_empty():
    assert compile.compile("hello") == ""
    assert compile.compile("") == ""
    assert compile.compile("anything at all") == ""


def test_let_x_I32():
    assert compile.compile("let x : I32 = 0;") == "int32_t x = 0;"


def test_let_y_I32():
    assert compile.compile("let y : I32 = 0;") == "int32_t y = 0;"


def test_let_x_I32_different_value():
    assert compile.compile("let x : I32 = 42;") == ""


def test_let_y_I32_negative_value():
    assert compile.compile("let y : I32 = -7;") == ""


def test_unsigned_types():
    assert compile.compile("let a : U8 = 0;") == "uint8_t a = 0;"
    assert compile.compile("let b : U16 = 0;") == "uint16_t b = 0;"
    assert compile.compile("let c : U32 = 0;") == "uint32_t c = 0;"
    assert compile.compile("let d : U64 = 0;") == "uint64_t d = 0;"


def test_signed_types():
    assert compile.compile("let e : I8 = 0;") == "int8_t e = 0;"
    assert compile.compile("let f : I16 = 0;") == "int16_t f = 0;"
    assert compile.compile("let g : I32 = 0;") == "int32_t g = 0;"
    assert compile.compile("let h : I64 = 0;") == "int64_t h = 0;"


def test_bool_types():
    assert compile.compile("let x : Bool = true;") == "bool x = true;"
    assert compile.compile("let x : Bool = false;") == "bool x = false;"


def test_numeric_comparisons():
    assert compile.compile("let x : I32 == 42;") == "int32_t x = x == 42;"
    assert compile.compile("let x : I32 != 42;") == "int32_t x = x != 42;"
    assert compile.compile("let x : I32 < 42;") == "int32_t x = x < 42;"
    assert compile.compile("let x : I32 > 42;") == "int32_t x = x > 42;"
    assert compile.compile("let x : I32 <= 42;") == "int32_t x = x <= 42;"
    assert compile.compile("let x : I32 >= 42;") == "int32_t x = x >= 42;"


def test_if_statement():
    assert compile.compile("if (x == 1) { x = 2; }") == "if (x == 1) { x = 2; }"
    assert compile.compile("if (y < 5) { y = y + 1; }") == "if (y < 5) { y = y + 1; }"
    # Should not match if missing parentheses or braces
    assert compile.compile("if x == 1 { x = 2; }") == ""
    assert compile.compile("if (x == 1) x = 2; }") == ""
    assert compile.compile("if (x == 1) { x = 2; ") == ""


def test_if_else_statement():
    assert (
        compile.compile("if (x == 1) { x = 2; } else { x = 3; }")
        == "if (x == 1) { x = 2; } else { x = 3; }"
    )
    assert (
        compile.compile("if (y < 5) { y = y + 1; } else { y = 0; }")
        == "if (y < 5) { y = y + 1; } else { y = 0; }"
    )
    # Should not match if missing braces or structure
    assert compile.compile("if (x == 1) { x = 2; } else x = 3; }") == ""
    assert compile.compile("if (x == 1) { x = 2; } else { x = 3; ") == ""


def test_struct():
    assert compile.compile("struct Empty {}") == "struct Empty {};"
    assert compile.compile("struct MyStruct {}") == "struct MyStruct {};"
    # Should not match if missing braces or invalid name
    assert compile.compile("struct {}") == ""
    assert compile.compile("struct 123 {}") == ""


def test_empty_braces():
    assert compile.compile("{}") == "{}"


def test_let_in_braces():
    assert compile.compile("{let x = 1;}") == "{int x = 1;}"


def test_multiple_let_in_braces():
    assert compile.compile("{let x = 1; let y = 2;}") == "{int x = 1; int y = 2;}"
    assert (
        compile.compile("{let a = 10; let b = 20; let c = 30;}")
        == "{int a = 10; int b = 20; int c = 30;}"
    )


def test_multiple_let_outside_braces():
    assert compile.compile("let x = 0; let y = x;") == "int x = 0; int y = x;"


def test_fn_empty():
    assert compile.compile("fn empty() : Void => {}") == "void empty(){}"


def test_fn_accept():
    assert (
        compile.compile("fn accept(value : I32) : Void => {}")
        == "void accept(int32_t value){}"
    )


def test_fn_get():
    assert (
        compile.compile("fn get() : I32 => {return 0;}") == "int32_t get(){return 0;}"
    )


def test_while_statement():
    assert (
        compile.compile("while (x < 10) { x = x + 1; }")
        == "while (x < 10) { x = x + 1; }"
    )
    assert (
        compile.compile("while (y != 0) { y = y - 1; }")
        == "while (y != 0) { y = y - 1; }"
    )
    # Should not match if missing parentheses or braces
    assert compile.compile("while x < 10 { x = x + 1; }") == ""
    assert compile.compile("while (x < 10) x = x + 1; }") == ""
    assert compile.compile("while (x < 10) { x = x + 1; ") == ""
