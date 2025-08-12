/// Compiles a simple assignment from a custom syntax to C syntax.
/// Specifically, translates 'let x : I32 = 0;' to 'int32_t x = 0;'.
pub fn compile(input: &str) -> Result<String, &'static str> {
    if input.trim() == "let x : I32 = 0;" {
        Ok("int32_t x = 0;".to_string())
    } else {
        Err("This function only compiles 'let x : I32 = 0;'")
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_compile_success() {
        let result = compile("let x : I32 = 0;");
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "int32_t x = 0;");
    }

    #[test]
    fn test_compile_error() {
        let result = compile("let y : I32 = 1;");
        assert!(result.is_err());
        assert_eq!(result.unwrap_err(), "This function only compiles 'let x : I32 = 0;'");
    }
}
