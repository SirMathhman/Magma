import sys
from pathlib import Path
import pytest

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "src"))

from magma import Compiler
from .utils import compile_source

def test_compile_call_with_expression_arg(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn second(): I32 => { return 0; }\nfn first(x: I32): Void => {}\nfn caller(): Void => { first(200 + second()); }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "int second() {\n    return 0;\n}\nvoid first(int x) {\n}\nvoid caller() {\n    first(200 + second());\n}\n"
    )


def test_compile_nested_while_with_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn run(): I32 => { if (true) { let x = 100; while (true) { let y = 200; return 0; } } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "int run() {\n    if (1) {\n        int x = 100;\n        while (1) {\n            int y = 200;\n            return 0;\n        }\n    }\n}\n"
    )


def test_compile_break_in_while(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn loop(): Void => { while (true) { break; } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void loop() {\n    while (1) {\n        break;\n    }\n}\n"
    )


def test_compile_continue_in_while(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn loop(): Void => { while (true) { continue; } }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void loop() {\n    while (1) {\n        continue;\n    }\n}\n"
    )


def test_compile_type_alias_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("type MyAlias = I16; fn foo(): Void => { let value: MyAlias = 100; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {\n    short value = 100;\n}\n"


def test_compile_global_struct_variable(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Point {x : I32; y : I32;}\n\nlet myPoint: Point;"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    int x;\n    int y;\n};\nstruct Point myPoint;\n"
    )


def test_compile_struct_variable_inside_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Point {x : I32; y : I32;}\nfn foo(): Void => { let p: Point; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    int x;\n    int y;\n};\nvoid foo() {\n    struct Point p;\n}\n"
    )


def test_compile_struct_variable_unknown_type(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo(): Void => { let p: Unknown; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "compiled: fn foo(): Void => { let p: Unknown; }"


def test_compile_global_function_pointer(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("let myEmpty: () => Void;")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void (*myEmpty)();\n"


def test_compile_function_pointer_in_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo(): Void => { let cb: () => Void; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {\n    void (*cb)();\n}\n"


def test_compile_function_pointer_with_params_global(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("let adder: (I32, I32) => I32;")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int (*adder)(int, int);\n"


def test_compile_function_pointer_with_params_in_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn foo(): Void => { let cb: (I32, I32) => I32; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void foo() {\n    int (*cb)(int, int);\n}\n"


def test_compile_function_param_function_type(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn call(cb: () => Void): Void => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "void call(void (*cb)()) {\n}\n"


def test_compile_class_fn_param_function_type(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("class fn Handler(cb: () => Void) => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Handler {\n    void (*cb)();\n};\nstruct Handler Handler(void (*cb)()) {\n    struct Handler this;\n    this.cb = cb;\n    return this;\n}\n"
    )


def test_compile_struct_init_global(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Point {x : I32; y : I32;}\n\nlet myPoint = Point {3, 4};"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    int x;\n    int y;\n};\nstruct Point myPoint;\nmyPoint.x = 3;\nmyPoint.y = 4;\n"
    )


def test_compile_struct_init_in_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Point {x : I32; y : I32;}\nfn foo(): Void => { let p = Point {3, 4}; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    int x;\n    int y;\n};\nvoid foo() {\n    struct Point p;\n    p.x = 3;\n    p.y = 4;\n}\n"
    )


def test_compile_generic_struct_monomorph(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Wrapper<T> {value : T}\n\nlet value: Wrapper<I32> = Wrapper {100};"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Wrapper_I32 {\n    int value;\n};\nstruct Wrapper_I32 value;\nvalue.value = 100;\n"
    )


def test_compile_generic_struct_with_pointer(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Ptr<T> { inner: *T }\nlet value: I32 = 5;\nlet p: Ptr<I32>;"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Ptr_I32 {\n    int* inner;\n};\nint value = 5;\nstruct Ptr_I32 p;\n"
    )


def test_compile_struct_literal_field_access(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "struct Wrapper { value : I32 }\nfn foo(): Void => { let inner = (Wrapper {100}).value; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Wrapper {\n    int value;\n};\nvoid foo() {\n    int inner = 100;\n}\n"
    )


def test_compile_class_fn_shorthand(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("class fn Point(x: U32, y: U32) => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    unsigned int x;\n    unsigned int y;\n};\nstruct Point Point(unsigned int x, unsigned int y) {\n    struct Point this;\n    this.x = x;\n    this.y = y;\n    return this;\n}\n"
    )


def test_compile_generic_class_fn(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "class fn Wrapper<T>(value : T) => {}\n\nlet value : Wrapper<I32>;"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Wrapper_I32 {\n    int value;\n};\nstruct Wrapper_I32 value;\nstruct Wrapper_I32 Wrapper_I32(int value) {\n    struct Wrapper_I32 this;\n    this.value = value;\n    return this;\n}\n"
    )


def test_compile_unused_generic_class_fn(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("class fn Box<T>() => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == ""


def test_compile_unused_generic_fn(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn malloc<T>() => {}")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == ""


def test_compile_class_fn_with_method(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "class fn Point(x: U32, y: U32) => { fn empty() => { } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    unsigned int x;\n    unsigned int y;\n};\nvoid empty_Point(struct Point this) {\n}\nstruct Point Point(unsigned int x, unsigned int y) {\n    struct Point this;\n    this.x = x;\n    this.y = y;\n    return this;\n}\n"
    )


def test_compile_class_fn_method_return_this(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "class fn Point(x: U32, y: U32) => { fn empty() => { return this; } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Point {\n    unsigned int x;\n    unsigned int y;\n};\nstruct Point empty_Point(struct Point this) {\n    return this;\n}\nstruct Point Point(unsigned int x, unsigned int y) {\n    struct Point this;\n    this.x = x;\n    this.y = y;\n    return this;\n}\n"
    )


def test_compile_flatten_inner_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn outer() => { fn inner() => {} }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct outer_t {\n};\nvoid inner_outer(struct outer_t this) {\n}\nvoid outer() {\n}\n"
    )


def test_compile_inner_function_with_declaration(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn parent() => { let myValue : I32 = 100; fn child(something : I32) => { } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct parent_t {\n    int myValue;\n};\nvoid child_parent(struct parent_t this, int something) {\n}\nvoid parent() {\n    struct parent_t this;\n    this.myValue = 100;\n}\n"
    )


def test_compile_inner_function_call_passes_env(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn outer() => { let value: I32 = 1; fn inner() => { } inner(); }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct outer_t {\n    int value;\n};\nvoid inner_outer(struct outer_t this) {\n}\nvoid outer() {\n    struct outer_t this;\n    this.value = 1;\n    inner_outer(this);\n}\n"
    )


def test_compile_inner_function_param_capture(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn outer(myParam : I32) => { fn inner() => { } }")
    
def test_compile_inner_function_with_inferred_declaration(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn parent() => { let first = 100; fn child(something : I32) => { } }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct parent_t {\n    int first;\n};\nvoid child_parent(struct parent_t this, int something) {\n}\nvoid parent() {\n    struct parent_t this;\n    this.first = 100;\n}\n"
    )


def test_compile_implicit_int_return(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("fn first() => { return 100; }")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int first() {\n    return 100;\n}\n"


def test_compile_class_fn_return_consistency(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "class fn Item() => {}\n\nclass fn Factory() => {\n fn create() => {\n  return Item();\n }\n}"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Item {\n};\nstruct Factory {\n};\nstruct Item Item() {\n    struct Item this;\n    return this;\n}\nstruct Item create_Factory(struct Factory this) {\n    return Item();\n}\nstruct Factory Factory() {\n    struct Factory this;\n    return this;\n}\n"
    )


def test_compile_empty_class_with_method_global_instance(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "class fn Car() => {\n fn drive() => {\n }\n}\n\nlet car : Car = Car();"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Car {\n};\nstruct Car car = Car();\nvoid drive_Car(struct Car this) {\n}\nstruct Car Car() {\n    struct Car this;\n    return this;\n}\n"
    )


def test_compile_inline_class_instantiation(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("let value : Test = (class fn Test() => {})();")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "struct Test {\n};\nstruct Test value = Test();\nstruct Test Test() {\n    struct Test this;\n    return this;\n}\n"
    )


def test_compile_pointer_global(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("let value: I32 = 5;\nlet reference: *I32 = &value;")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert output_file.read_text() == "int value = 5;\nint* reference = &value;\n"


def test_compile_pointer_in_function(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text(
        "fn foo(): Void => { let value: I32 = 5; let reference: *I32 = &value; }"
    )
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "void foo() {\n    int value = 5;\n    int* reference = &value;\n}\n"
    )


def test_compile_dereference_global(tmp_path):
    compiler = Compiler()
    input_file = tmp_path / "input.mg"
    input_file.write_text("let reference: *I32;\nlet dereference: I32 = *reference;")
    output_file = tmp_path / "out.c"

    compiler.compile(input_file, output_file)

    assert (
        output_file.read_text()
        == "int* reference;\nint dereference = *reference;\n"
    )


def test_compile_object_basic(tmp_path):
    output = compile_source(tmp_path, "object Singleton {}")

    expected = (
        "struct Singleton {\n"
        "};\n"
        "struct Singleton Singleton() {\n"
        "    static int init;\n"
        "    static struct Singleton this;\n"
        "    if (!init) {\n"
        "        init = 1;\n"
        "    }\n"
        "    return this;\n"
        "}\n"
    )

    assert output == expected


def test_compile_object_with_body(tmp_path):
    output = compile_source(tmp_path, "object Once { let value: I32 = 0; }")

    expected = (
        "struct Once {\n"
        "};\n"
        "struct Once Once() {\n"
        "    static int init;\n"
        "    static struct Once this;\n"
        "    if (!init) {\n"
        "        int value = 0;\n"
        "        init = 1;\n"
        "    }\n"
        "    return this;\n"
        "}\n"
    )

    assert output == expected
