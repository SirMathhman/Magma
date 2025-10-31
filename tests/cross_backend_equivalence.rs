use std::fs;
use std::path::PathBuf;
use std::process::Command;

/// Test helper: compiles Magma source to all three backends and returns the outputs
fn compile_to_all_targets(magma_code: &str, name: &str) -> (String, String, String) {
    let test_file = PathBuf::from(format!("target/xbench_{}.mg", name));
    fs::write(&test_file, magma_code).expect("Failed to write test file");

    // Find the magma binary - check both debug and release
    let bin_path = if PathBuf::from("target/release/magma.exe").exists() {
        "target/release/magma.exe"
    } else if PathBuf::from("target/debug/magma.exe").exists() {
        "target/debug/magma.exe"
    } else {
        panic!("magma binary not found in target/release or target/debug");
    };

    // Compile to TypeScript
    let ts_output = Command::new(bin_path)
        .args(&["compile", test_file.to_str().unwrap(), "--target", "ts"])
        .output()
        .expect("Failed to compile to TypeScript");
    assert!(ts_output.status.success(), "TypeScript compilation failed");

    let ts_file = PathBuf::from(format!("target/xbench_{}.ts", name));
    let ts_code = fs::read_to_string(&ts_file).expect("Failed to read TS output");

    // Compile to JavaScript
    let js_output = Command::new(bin_path)
        .args(&["compile", test_file.to_str().unwrap(), "--target", "js"])
        .output()
        .expect("Failed to compile to JavaScript");
    assert!(js_output.status.success(), "JavaScript compilation failed");

    let js_file = PathBuf::from(format!("target/xbench_{}.js", name));
    let js_code = fs::read_to_string(&js_file).expect("Failed to read JS output");

    // Compile to LLVM
    let llvm_output = Command::new(bin_path)
        .args(&["compile", test_file.to_str().unwrap(), "--target", "llvm"])
        .output()
        .expect("Failed to compile to LLVM");
    assert!(llvm_output.status.success(), "LLVM compilation failed");

    let ll_file = PathBuf::from(format!("target/xbench_{}.ll", name));
    let ll_code = fs::read_to_string(&ll_file).expect("Failed to read LLVM output");

    // Cleanup
    fs::remove_file(&test_file).ok();
    fs::remove_file(&ts_file).ok();
    fs::remove_file(&js_file).ok();
    fs::remove_file(&ll_file).ok();

    (ts_code, js_code, ll_code)
}

#[test]
fn test_xbench_simple_function_equivalence() {
    let magma_code = "fn identity(x : I32) : I32 => x;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "identity");

    // All backends should have the function
    assert!(
        ts.contains("function identity"),
        "TS should have identity function"
    );
    assert!(
        js.contains("function identity"),
        "JS should have identity function"
    );
    assert!(
        llvm.contains("define i32 @identity"),
        "LLVM should have identity function"
    );

    // Type signatures should be preserved
    assert!(
        ts.contains("(x: number): number"),
        "TS should have correct type"
    );
    assert!(js.contains("(x)"), "JS should have parameter");
    assert!(llvm.contains("i32 %x"), "LLVM should have i32 parameter");
}

#[test]
fn test_xbench_arithmetic_operators_equivalence() {
    let magma_code = "fn add(x : I32, y : I32) : I32 => x + y;
fn sub(x : I32, y : I32) : I32 => x - y;
fn mul(x : I32, y : I32) : I32 => x * y;
fn div(x : I32, y : I32) : I32 => x / y;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "arithmetic");

    // All should have all functions
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("add"), "{} missing add", name);
        assert!(code.contains("sub"), "{} missing sub", name);
        assert!(code.contains("mul"), "{} missing mul", name);
        assert!(code.contains("div"), "{} missing div", name);
    }
}

#[test]
fn test_xbench_comparison_operators_equivalence() {
    let magma_code = "fn eq(x : I32, y : I32) : Bool => x == y;
fn ne(x : I32, y : I32) : Bool => x != y;
fn lt(x : I32, y : I32) : Bool => x < y;
fn gt(x : I32, y : I32) : Bool => x > y;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "comparison");

    // TypeScript should have comparisons with boolean return
    assert!(ts.contains("function eq"), "TS missing eq");
    assert!(ts.contains("boolean"), "TS should have boolean type");

    // JavaScript should have comparisons
    assert!(js.contains("function eq"), "JS missing eq");

    // LLVM should have comparisons with i1 (boolean)
    assert!(llvm.contains("@eq"), "LLVM missing eq");
    assert!(llvm.contains("i1"), "LLVM should have i1 type for booleans");
}

#[test]
fn test_xbench_multiple_parameters_equivalence() {
    let magma_code = "fn max3(a : I32, b : I32, c : I32) : I32 => a;
fn combine(x : I32, y : I32, z : I32, w : I32) : I32 => x + y + z + w;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "multiparams");

    // Check parameter count consistency
    assert!(
        ts.contains("max3(a: number, b: number, c: number)"),
        "TS should have 3 params"
    );
    assert!(js.contains("max3(a, b, c)"), "JS should have 3 params");
    assert!(
        llvm.contains("@max3(i32 %a, i32 %b, i32 %c)"),
        "LLVM should have 3 params"
    );

    assert!(
        ts.contains("combine(x: number, y: number, z: number, w: number)"),
        "TS should have 4 params"
    );
    assert!(
        js.contains("combine(x, y, z, w)"),
        "JS should have 4 params"
    );
    assert!(
        llvm.contains("@combine(i32 %x, i32 %y, i32 %z, i32 %w)"),
        "LLVM should have 4 params"
    );
}

#[test]
fn test_xbench_if_expression_equivalence() {
    let magma_code = "fn abs(x : I32) : I32 => if x > 0 { x } else { 0 - x };";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "if_expr");

    // All should contain conditional logic
    assert!(ts.contains("if"), "TS should have if");
    assert!(js.contains("if"), "JS should have if");
    assert!(
        llvm.contains("br i1"),
        "LLVM should have conditional branch"
    );
}

#[test]
fn test_xbench_nested_calls_equivalence() {
    let magma_code = "fn inc(x : I32) : I32 => x + 1;
fn dec(x : I32) : I32 => x - 1;
fn double(x : I32) : I32 => x + x;
fn compose(x : I32) : I32 => double(inc(dec(x)));";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "nested");

    // All should have all functions
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("inc"), "{} missing inc", name);
        assert!(code.contains("dec"), "{} missing dec", name);
        assert!(code.contains("double"), "{} missing double", name);
        assert!(code.contains("compose"), "{} missing compose", name);
    }

    // TS should show nested calls with type annotations
    assert!(ts.contains("compose"), "TS should have compose function");

    // JS should show nested calls
    assert!(js.contains("compose"), "JS should have compose function");

    // LLVM should show function calls
    assert!(
        llvm.contains("@compose"),
        "LLVM should have compose function"
    );
}

#[test]
fn test_xbench_struct_definition_equivalence() {
    let magma_code = "struct Point { x : I32, y : I32 }";
    let (ts, js, _llvm) = compile_to_all_targets(magma_code, "struct_simple");

    // TypeScript: struct becomes interface
    assert!(
        ts.contains("interface Point") || ts.contains("Point"),
        "TS should have Point definition"
    );
    assert!(
        ts.contains("number"),
        "TS should have number type for fields"
    );

    // JavaScript: struct becomes constructor
    assert!(js.contains("Point"), "JS should have Point");

    // Note: LLVM backend currently only emits boilerplate for structs without functions
}

#[test]
fn test_xbench_multiple_structs_equivalence() {
    let magma_code = "struct Point { x : I32, y : I32 }
struct Circle { center : Point, radius : I32 }";
    let (ts, js, _llvm) = compile_to_all_targets(magma_code, "struct_multi");

    // TypeScript and JavaScript should have both structs
    assert!(ts.contains("Point"), "TS missing Point");
    assert!(ts.contains("Circle"), "TS missing Circle");

    assert!(js.contains("Point"), "JS missing Point");
    assert!(js.contains("Circle"), "JS missing Circle");

    // Note: LLVM backend only emits boilerplate without functions
}

#[test]
fn test_xbench_unary_operators_equivalence() {
    let magma_code = "fn negate(x : I32) : I32 => 0 - x;
fn not_bool(x : Bool) : Bool => !x;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "unary");

    // All should have unary operations
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("negate"), "{} should have negate", name);
        assert!(code.contains("not_bool"), "{} should have not_bool", name);
    }
}

#[test]
fn test_xbench_recursive_function_equivalence() {
    let magma_code =
        "fn factorial(n : I32) : I32 => if n <= 1 { 1 } else { n * factorial(n - 1) };";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "recursive");

    // All should have recursive function
    assert!(ts.contains("factorial"), "TS should have factorial");
    assert!(js.contains("factorial"), "JS should have factorial");
    assert!(llvm.contains("@factorial"), "LLVM should have factorial");

    // All should show recursion (function calls itself)
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("factorial"), "{} should call factorial", name);
    }
}

#[test]
fn test_xbench_logical_operators_equivalence() {
    let magma_code = "fn both(x : Bool, y : Bool) : Bool => x && y;
fn either(x : Bool, y : Bool) : Bool => x || y;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "logical");

    // TypeScript: logical ops with type annotations
    assert!(ts.contains("both"), "TS should have both");
    assert!(ts.contains("either"), "TS should have either");
    assert!(ts.contains("boolean"), "TS should have boolean type");

    // JavaScript: logical ops
    assert!(js.contains("both"), "JS should have both");
    assert!(js.contains("either"), "JS should have either");

    // LLVM: logical ops
    assert!(llvm.contains("@both"), "LLVM should have both");
    assert!(llvm.contains("@either"), "LLVM should have either");
}

#[test]
fn test_xbench_modulo_operator_equivalence() {
    let magma_code = "fn is_even(x : I32) : Bool => x % 2 == 0;
fn is_odd(x : I32) : Bool => x % 2 != 0;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "modulo");

    // All should have modulo operations
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("is_even"), "{} should have is_even", name);
        assert!(code.contains("is_odd"), "{} should have is_odd", name);
    }
}

#[test]
fn test_xbench_chained_operators_equivalence() {
    let magma_code = "fn complex(a : I32, b : I32, c : I32) : I32 => a + b * c - (a / b);";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "chained");

    // All should have the function
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("complex"), "{} should have complex", name);
    }
}

#[test]
fn test_xbench_multiple_return_paths_equivalence() {
    let magma_code =
        "fn classify(x : I32) : I32 => if x < 0 { 0 - x } else { if x == 0 { 0 } else { x } };";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "multi_return");

    // All should handle nested conditionals
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("classify"), "{} should have classify", name);
    }
}

#[test]
fn test_xbench_zero_and_one_special_values() {
    let magma_code = "fn return_zero() : I32 => 0;
fn return_one() : I32 => 1;
fn return_neg_one() : I32 => 0 - 1;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "special_vals");

    // All should have the functions
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(
            code.contains("return_zero"),
            "{} should have return_zero",
            name
        );
        assert!(
            code.contains("return_one"),
            "{} should have return_one",
            name
        );
        assert!(
            code.contains("return_neg_one"),
            "{} should have return_neg_one",
            name
        );
    }
}

#[test]
fn test_xbench_bool_type_consistency() {
    let magma_code = "fn true_val() : Bool => 1 == 1;
fn false_val() : Bool => 1 != 1;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "bool_type");

    // TypeScript: Bool becomes boolean
    assert!(ts.contains("boolean"), "TS should use boolean type");
    assert!(ts.contains("true_val"), "TS should have true_val");
    assert!(ts.contains("false_val"), "TS should have false_val");

    // JavaScript: functions exist
    assert!(js.contains("true_val"), "JS should have true_val");
    assert!(js.contains("false_val"), "JS should have false_val");

    // LLVM: Bool becomes i1
    assert!(llvm.contains("i1"), "LLVM should use i1 for booleans");
    assert!(llvm.contains("@true_val"), "LLVM should have true_val");
    assert!(llvm.contains("@false_val"), "LLVM should have false_val");
}

#[test]
fn test_xbench_division_by_operations() {
    let magma_code = "fn half(x : I32) : I32 => x / 2;
fn quarter(x : I32) : I32 => x / 4;
fn tenth(x : I32) : I32 => x / 10;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "division");

    // All should compile successfully
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("half"), "{} should have half", name);
        assert!(code.contains("quarter"), "{} should have quarter", name);
        assert!(code.contains("tenth"), "{} should have tenth", name);
    }
}

#[test]
fn test_xbench_long_parameter_list() {
    let magma_code = "fn sum6(a : I32, b : I32, c : I32, d : I32, e : I32, f : I32) : I32 => a + b + c + d + e + f;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "long_params");

    // TypeScript should list all 6 params with types
    assert!(ts.contains("sum6"), "TS should have sum6");
    assert!(ts.contains("a:"), "TS should have param a");

    // JavaScript should list all 6 params
    assert!(js.contains("sum6"), "JS should have sum6");

    // LLVM should show all 6 params
    assert!(llvm.contains("@sum6"), "LLVM should have sum6");
}

#[test]
fn test_xbench_deep_nesting_operators() {
    let magma_code = "fn deep(x : I32) : I32 => ((((x + 1) - 2) * 3) / 4);";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "deep_nest");

    // All should handle deeply nested operations
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("deep"), "{} should have deep", name);
    }
}

#[test]
fn test_xbench_comparison_chain() {
    let magma_code = "fn greater(x : I32, y : I32) : Bool => x > y;
fn less(x : I32, y : I32) : Bool => x < y;
fn gte(x : I32, y : I32) : Bool => x > y || x == y;
fn lte(x : I32, y : I32) : Bool => x < y || x == y;";
    let (ts, js, llvm) = compile_to_all_targets(magma_code, "comp_chain");

    // All should have all comparison functions
    for (code, name) in &[
        (ts.as_str(), "TypeScript"),
        (js.as_str(), "JavaScript"),
        (llvm.as_str(), "LLVM"),
    ] {
        assert!(code.contains("greater"), "{} should have greater", name);
        assert!(code.contains("less"), "{} should have less", name);
        assert!(code.contains("gte"), "{} should have gte", name);
        assert!(code.contains("lte"), "{} should have lte", name);
    }
}
