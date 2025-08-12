/// Compiles a simple assignment from a custom syntax to C syntax.
/// Specifically, translates 'let x : I32 = 0;' to 'int32_t x = 0;'.
pub fn compile(input: &str) -> Result<String, &'static str> {
    let input = input.trim();
    let re = regex::Regex::new(r"^let\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*:\s*I32\s*=\s*([0-9]+);\s*$").unwrap();
    if let Some(caps) = re.captures(input) {
        let name = &caps[1];
        let value = &caps[2];
        return Ok(format!("int32_t {} = {};", name, value));
    }
    Err("This function only compiles 'let <name> : I32 = <int>;'")
}

#[cfg(test)]
mod tests {
    use super::*;

    // Needed for regex
    extern crate regex;

    #[test]
    fn test_compile_success() {
        let result = compile("let x : I32 = 0;");
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "int32_t x = 0;");
    }

    #[test]
    fn test_compile_200() {
        let result = compile("let x : I32 = 200;");
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "int32_t x = 200;");
    }

    #[test]
    fn test_compile_error() {
        let result = compile("let y : I32 = foo;"); // invalid value
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "This function only compiles 'let <name> : I32 = <int>;'");
    }

    #[test]
    fn test_different_name() {
        let result = compile("let foo : I32 = 0;");
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "int32_t foo = 0;");
    }
}
