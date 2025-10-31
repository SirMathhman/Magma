use std::fs;
use std::path::PathBuf;
use std::process::Command;

#[test]
fn test_cli_compile_typescript() {
    // Create a temporary test file
    let test_code = "fn test(x : I32) : I32 => x + 1;";
    let test_file = PathBuf::from("target/test_hello.mg");
    fs::write(&test_file, test_code).expect("Failed to write test file");

    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&["compile", test_file.to_str().unwrap(), "--target", "ts"])
        .output()
        .expect("Failed to run magma compile");

    assert!(
        output.status.success(),
        "CLI failed: {}",
        String::from_utf8_lossy(&output.stderr)
    );

    // Check that output file was created
    let out_file = PathBuf::from("target/test_hello.ts");
    assert!(
        out_file.exists(),
        "Output file not created: {}",
        out_file.display()
    );

    let content = fs::read_to_string(&out_file).expect("Failed to read output");
    assert!(
        content.contains("function test"),
        "Output should contain function definition"
    );

    // Cleanup
    fs::remove_file(&test_file).ok();
    fs::remove_file(&out_file).ok();
}

#[test]
fn test_cli_compile_javascript() {
    let test_code = "fn add(a : I32, b : I32) : I32 => a + b;";
    let test_file = PathBuf::from("target/test_add.mg");
    fs::write(&test_file, test_code).expect("Failed to write test file");

    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&["compile", test_file.to_str().unwrap(), "--target", "js"])
        .output()
        .expect("Failed to run magma compile");

    assert!(
        output.status.success(),
        "CLI failed: {}",
        String::from_utf8_lossy(&output.stderr)
    );

    let out_file = PathBuf::from("target/test_add.js");
    assert!(out_file.exists(), "Output file not created");

    let content = fs::read_to_string(&out_file).expect("Failed to read output");
    assert!(
        content.contains("function add"),
        "Output should contain function definition"
    );

    // Cleanup
    fs::remove_file(&test_file).ok();
    fs::remove_file(&out_file).ok();
}

#[test]
fn test_cli_compile_llvm() {
    let test_code = "fn id(x : I32) : I32 => x;";
    let test_file = PathBuf::from("target/test_id.mg");
    fs::write(&test_file, test_code).expect("Failed to write test file");

    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&["compile", test_file.to_str().unwrap(), "--target", "llvm"])
        .output()
        .expect("Failed to run magma compile");

    assert!(
        output.status.success(),
        "CLI failed: {}",
        String::from_utf8_lossy(&output.stderr)
    );

    let out_file = PathBuf::from("target/test_id.ll");
    assert!(out_file.exists(), "Output file not created");

    let content = fs::read_to_string(&out_file).expect("Failed to read output");
    assert!(
        content.contains("define"),
        "Output should contain LLVM function definition"
    );

    // Cleanup
    fs::remove_file(&test_file).ok();
    fs::remove_file(&out_file).ok();
}

#[test]
fn test_cli_custom_output_file() {
    let test_code = "fn f(x : I32) : I32 => x;";
    let test_file = PathBuf::from("target/test_custom.mg");
    let output_file = PathBuf::from("target/my_output.ts");

    fs::write(&test_file, test_code).expect("Failed to write test file");

    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&[
            "compile",
            test_file.to_str().unwrap(),
            "--output",
            output_file.to_str().unwrap(),
        ])
        .output()
        .expect("Failed to run magma compile");

    assert!(
        output.status.success(),
        "CLI failed: {}",
        String::from_utf8_lossy(&output.stderr)
    );
    assert!(output_file.exists(), "Custom output file not created");

    // Cleanup
    fs::remove_file(&test_file).ok();
    fs::remove_file(&output_file).ok();
}

#[test]
fn test_cli_lex_command() {
    let test_code = "fn test(x : I32) : I32 => x;";
    let test_file = PathBuf::from("target/test_lex.mg");
    fs::write(&test_file, test_code).expect("Failed to write test file");

    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&["lex", test_file.to_str().unwrap()])
        .output()
        .expect("Failed to run magma lex");

    assert!(output.status.success(), "Lex command failed");

    let stdout = String::from_utf8_lossy(&output.stdout);
    assert!(stdout.contains("fn"), "Output should contain fn keyword");
    assert!(stdout.contains("test"), "Output should contain identifier");

    // Cleanup
    fs::remove_file(&test_file).ok();
}

#[test]
fn test_cli_parse_command() {
    let test_code = "fn test(x : I32) : I32 => x;";
    let test_file = PathBuf::from("target/test_parse.mg");
    fs::write(&test_file, test_code).expect("Failed to write test file");

    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&["parse", test_file.to_str().unwrap()])
        .output()
        .expect("Failed to run magma parse");

    assert!(output.status.success(), "Parse command failed");

    let stdout = String::from_utf8_lossy(&output.stdout);
    assert!(
        stdout.contains("AST from"),
        "Output should contain AST header"
    );
    assert!(stdout.contains("Items:"), "Output should show item count");

    // Cleanup
    fs::remove_file(&test_file).ok();
}

#[test]
fn test_cli_invalid_target() {
    let test_code = "fn f(x : I32) : I32 => x;";
    let test_file = PathBuf::from("target/test_invalid.mg");
    fs::write(&test_file, test_code).expect("Failed to write test file");

    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&[
            "compile",
            test_file.to_str().unwrap(),
            "--target",
            "invalid",
        ])
        .output()
        .expect("Failed to run magma compile");

    assert!(!output.status.success(), "Should fail with invalid target");

    let stderr = String::from_utf8_lossy(&output.stderr);
    assert!(
        stderr.contains("invalid target"),
        "Error message should mention invalid target"
    );

    // Cleanup
    fs::remove_file(&test_file).ok();
}

#[test]
fn test_cli_missing_input_file() {
    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&["compile"])
        .output()
        .expect("Failed to run magma compile");

    assert!(!output.status.success(), "Should fail with missing input");

    let stderr = String::from_utf8_lossy(&output.stderr);
    assert!(
        stderr.contains("Usage:") || stderr.contains("no input"),
        "Error message should guide user"
    );
}

#[test]
fn test_cli_version_flag() {
    let output = Command::new(env!("CARGO_BIN_EXE_magma"))
        .args(&["--version"])
        .output()
        .expect("Failed to run magma --version");

    assert!(output.status.success(), "Version command should succeed");

    let stdout = String::from_utf8_lossy(&output.stdout);
    assert!(stdout.contains("Magma"), "Output should contain 'Magma'");
    assert!(
        stdout.contains("0.1.0"),
        "Output should contain version number"
    );
}
