import pytest
from .utils import compile_source, CompileTimeout


def test_compile_empty_input_creates_empty_main(tmp_path):
    output = compile_source(tmp_path, "")

    assert output == "int main() {\n}\n"


def test_compile_non_empty_returns_placeholder(tmp_path):
    with pytest.raises(CompileTimeout):
        compile_source(tmp_path, "hello")


def test_compile_simple_function(tmp_path):
    output = compile_source(tmp_path, "fn foo() => {}")

    assert output == "void foo() {\n}\n"



def test_compile_multiple_functions(tmp_path):
    output = compile_source(tmp_path, "fn foo() => {}\nfn bar() => {}")

    assert output == "void foo() {\n}\nvoid bar() {\n}\n"


def test_compile_explicit_void_return(tmp_path):
    output = compile_source(tmp_path, "fn empty(): Void => {}")

    assert output == "void empty() {\n}\n"


def test_compile_bool_return(tmp_path):
    output = compile_source(tmp_path, "fn truth(): Bool => { return true; }")

    assert output == "int truth() {\n    return 1;\n}\n"


def test_compile_bool_false_return(tmp_path):
    output = compile_source(tmp_path, "fn lie(): Bool => { return false; }")

    assert output == "int lie() {\n    return 0;\n}\n"


def test_compile_function_with_extra_whitespace(tmp_path):
    output = compile_source(tmp_path, "fn   spaced  (  )  :  Void  =>  {   }")

    assert output == "void spaced() {\n}\n"


def test_compile_bool_with_whitespace(tmp_path):
    output = compile_source(tmp_path, "fn ws(): Bool => {  return true;  }")

    assert output == "int ws() {\n    return 1;\n}\n"


def test_compile_function_with_newlines(tmp_path):
    output = compile_source(tmp_path, "fn nl\n(\n)\n:\nVoid\n=>\n{\n}\n")

    assert output == "void nl() {\n}\n"

def test_compile_function_with_carriage_returns(tmp_path):
    content = "fn cr\r\n(\r\n)\r\n:\r\nVoid\r\n=>\r\n{\r\n}\r\n"
    output = compile_source(tmp_path, content)

    assert output == "void cr() {\n}\n"





@pytest.mark.parametrize(
    "magma_type,c_type",
    [
        ("U8", "unsigned char"),
        ("U16", "unsigned short"),
        ("U32", "unsigned int"),
        ("U64", "unsigned long long"),
        ("I8", "signed char"),
        ("I16", "short"),
        ("I32", "int"),
        ("I64", "long long"),
    ],
)
def test_compile_numeric_return(tmp_path, magma_type, c_type):
    output = compile_source(tmp_path, f"fn num(): {magma_type} => {{ return 0; }}")

    assert output == f"{c_type} num() {{\n    return 0;\n}}\n"


def test_compile_let_numeric(tmp_path):
    output = compile_source(tmp_path, "fn foo(): Void => { let myValue: I32 = 420; }")

    assert output == "void foo() {\n    int myValue = 420;\n}\n"


def test_compile_let_bool(tmp_path):
    output = compile_source(tmp_path, "fn flag(): Void => { let flag: Bool = true; }")

    assert output == "void flag() {\n    int flag = 1;\n}\n"


def test_compile_let_invalid_value(tmp_path):
    output = compile_source(tmp_path, "fn bad(): Void => { let nope: I32 = true; }")

    assert output == "compiled: fn bad(): Void => { let nope: I32 = true; }"

def test_compile_let_array_numeric(tmp_path):
    output = compile_source(tmp_path, "fn arr(): Void => { let myArray: [I32; 3] = [1, 2, 3]; }")

    assert output == "void arr() {\n    int myArray[] = {1, 2, 3};\n}\n"


def test_compile_let_array_invalid_size(tmp_path):
    output = compile_source(tmp_path, "fn badarr(): Void => { let arr: [I32; 2] = [1, 2, 3]; }")

    assert output == "compiled: fn badarr(): Void => { let arr: [I32; 2] = [1, 2, 3]; }"


def test_compile_let_infer_bool(tmp_path):
    output = compile_source(tmp_path, "fn infer(): Void => { let value = false; }")

    assert output == "void infer() {\n    int value = 0;\n}\n"


def test_compile_let_infer_numeric(tmp_path):
    output = compile_source(tmp_path, "fn infer(): Void => { let myInt = 100; }")

    assert output == "void infer() {\n    int myInt = 100;\n}\n"


def test_compile_let_declaration_no_value(tmp_path):
    output = compile_source(tmp_path, "fn decl(): Void => { let value: I16; }")

    assert output == "void decl() {\n    short value;\n}\n"


def test_compile_let_void_assignment_invalid(tmp_path):
    output = compile_source(tmp_path, "fn bad(): Void => { let nothing: Void = 0; }")

    assert output == "compiled: fn bad(): Void => { let nothing: Void = 0; }"


def test_compile_assignment_mut_numeric(tmp_path):
    output = compile_source(tmp_path, "fn assign(): Void => { let mut x: I32 = 100; x = 200; }")

    assert output == "void assign() {\n    int x = 100;\n    x = 200;\n}\n"


def test_compile_assignment_mut_bool(tmp_path):
    output = compile_source(tmp_path, "fn assign(): Void => { let mut flag: Bool = true; flag = false; }")

    assert output == "void assign() {\n    int flag = 1;\n    flag = 0;\n}\n"


def test_compile_assignment_without_mut_invalid(tmp_path):
    output = compile_source(tmp_path, "fn bad(): Void => { let x: I32 = 100; x = 200; }")

    assert output == "compiled: fn bad(): Void => { let x: I32 = 100; x = 200; }"


def test_compile_assignment_type_mismatch_invalid(tmp_path):
    output = compile_source(tmp_path, "fn bad(): Void => { let mut x: I32 = 100; x = true; }")

    assert output == "compiled: fn bad(): Void => { let mut x: I32 = 100; x = true; }"

def test_compile_struct_simple(tmp_path):
    output = compile_source(tmp_path, "struct Point {x : I32; y : I32}")

    assert output == "struct Point {\n    int x;\n    int y;\n};\n"


def test_compile_struct_bool_field(tmp_path):
    output = compile_source(tmp_path, "struct Flag {value : Bool}")

    assert output == "struct Flag {\n    int value;\n};\n"


def test_compile_struct_function_field(tmp_path):
    output = compile_source(tmp_path, "struct Test { doSomething: () => Void }")

    assert output == "struct Test {\n    void (*doSomething)();\n};\n"


def test_compile_struct_invalid_type(tmp_path):
    output = compile_source(tmp_path, "struct Bad {x : Unknown}")

    assert output == "compiled: struct Bad {x : Unknown}"


def test_compile_enum_simple(tmp_path):
    output = compile_source(tmp_path, "enum MyEnum { First, Second }")

    assert output == "enum MyEnum { First, Second };\n"


def test_compile_struct_enum_simple(tmp_path):
    output = compile_source(tmp_path, "struct enum Option { Some, None }")

    assert (
        output
        == "struct Option {\n"
        "    enum OptionTag tag;\n"
        "    union {\n"
        "        struct Some Some;\n"
        "        struct None None;\n"
        "    } data;\n"
        "};\n"
        "enum OptionTag { Some, None };\n"
    )


def test_compile_struct_enum_variant_fields(tmp_path):
    output = compile_source(tmp_path, "struct enum Option { Some(value: I32), None }")

    assert (
        output
        == "struct Some {\n"
        "    int value;\n"
        "};\n"
        "struct Option {\n"
        "    enum OptionTag tag;\n"
        "    union {\n"
        "        struct Some Some;\n"
        "        struct None None;\n"
        "    } data;\n"
        "};\n"
        "enum OptionTag { Some, None };\n"
    )



def test_compile_struct_enum_instance(tmp_path):
    output = compile_source(
        tmp_path,
        """struct enum Option { Some(value: I32), None }

let myOption0: Option.Some = Option.Some(5);
let myOption1: Option.None = Option.None();""",
    )

    assert (
        output
        == "struct Some {\n"
        "    int value;\n"
        "};\n"
        "struct Option {\n"
        "    enum OptionTag tag;\n"
        "    union {\n"
        "        struct Some Some;\n"
        "        struct None None;\n"
        "    } data;\n"
        "};\n"
        "enum OptionTag { Some, None };\n"
        "struct Option myOption0;\n"
        "myOption0.tag = Some;\n"
        "myOption0.data.Some.value = 5;\n"
        "struct Option myOption1;\n"
        "myOption1.tag = None;\n"
    )

def test_compile_extern_function_no_params(tmp_path):
    output = compile_source(tmp_path, "extern fn empty();")
    assert output == ""


def test_compile_extern_function_with_params(tmp_path):
    output = compile_source(tmp_path, "extern fn add(x: I32, y: I32): I32;")
    assert output == ""
