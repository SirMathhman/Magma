// Tests for loop support (for and while loops)
// These tests follow TDD: they will fail until loops are implemented

use magma::lexer::Lexer;
use magma::parser::Parser;

fn parse_program(input: &str) -> Result<magma::ast::Program, Vec<magma::CompilationError>> {
    let lexer = Lexer::new(input);
    let tokens = lexer.tokenize()?;
    let parser = Parser::new(tokens);
    parser.parse_program()
}

// While loop tests - simplified to not use assignment (not yet supported)
#[test]
fn test_while_loop_simple_parsing() {
    let source = "fn count_to_five() : I32 => { while 0 < 5 { 1 } };";
    let program = parse_program(source).expect("Failed to parse while loop");
    assert!(
        !program.items.is_empty(),
        "Program should have at least one item"
    );
}

#[test]
fn test_while_loop_with_break_parsing() {
    let source =
        "fn early_exit() : I32 => { while 1 < 100 { if 10 == 10 { break } else { 1 }; } };";
    let program = parse_program(source).expect("Failed to parse while with break");
    assert!(!program.items.is_empty(), "Program should have function");
}

#[test]
fn test_while_loop_with_continue_parsing() {
    let source = "fn skip_even() : I32 => { while 1 == 1 { if 10 > 5 { continue } else { 1 }; } };";
    let program = parse_program(source).expect("Failed to parse while with continue");
    assert!(!program.items.is_empty(), "Program should have function");
}

// For loop tests - simplified
#[test]
fn test_for_loop_range_parsing() {
    let source = "fn sum_range() : I32 => { for i in 0..10 { i } };";
    let program = parse_program(source).expect("Failed to parse for loop");
    assert!(!program.items.is_empty(), "Program should have function");
}

#[test]
fn test_for_loop_with_break_parsing() {
    let source =
        "fn find_first() : I32 => { for i in 0..100 { if i == 42 { break } else { 0 }; }; 42 };";
    let program = parse_program(source).expect("Failed to parse for with break");
    assert!(!program.items.is_empty(), "Program should have function");
}

#[test]
fn test_for_loop_with_continue_parsing() {
    let source =
        "fn skip_loop() : I32 => { for i in 0..10 { if i == 5 { continue } else { i }; } };";
    let program = parse_program(source).expect("Failed to parse for with continue");
    assert!(!program.items.is_empty(), "Program should have function");
}

// Nested loops
#[test]
fn test_nested_loops_parsing() {
    let source = "fn multiplication_table() : I32 => { for i in 0..5 { for j in 0..5 { i } } };";
    let program = parse_program(source).expect("Failed to parse nested loops");
    assert!(!program.items.is_empty(), "Program should have function");
}

// Loop variable scope
#[test]
fn test_loop_variable_scope_parsing() {
    let source = "fn loop_scope() : I32 => { for i in 0..10 { i } };";
    let program = parse_program(source).expect("Failed to parse loop variable scope");
    assert!(!program.items.is_empty(), "Program should have function");
}

// Loop with function calls
#[test]
fn test_loop_with_function_calls_parsing() {
    let source = "fn helper(x : I32) : I32 => x * 2; fn loop_with_calls() : I32 => { for i in 0..5 { helper(i) } };";
    let program = parse_program(source).expect("Failed to parse loop with function calls");
    assert_eq!(program.items.len(), 2, "Program should have two functions");
}

// Multiple breaks and continues
#[test]
fn test_multiple_control_flow_parsing() {
    let source = "fn complex() : I32 => { for i in 0..20 { if i == 5 { continue } else { 0 }; if i == 15 { break } else { 0 }; } };";
    let program = parse_program(source).expect("Failed to parse multiple control flow");
    assert!(!program.items.is_empty(), "Program should have function");
}

// While with no break condition (infinite structure - should still parse)
#[test]
fn test_while_true_parsing() {
    let source = "fn loop_forever() : I32 => { while 1 == 1 { break }; 42 };";
    let program = parse_program(source).expect("Failed to parse while true");
    assert!(!program.items.is_empty(), "Program should have function");
}
