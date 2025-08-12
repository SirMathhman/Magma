/// Compiles a simple assignment from a custom syntax to C syntax.
/// Specifically, translates 'let x : I32 = 0;' to 'int32_t x = 0;'.
pub fn compile(input: &str) -> Result<String, &'static str> {
    let input = input.trim();
    // Updated regex: allow optional type suffix after integer literal
    let re = regex::Regex::new(r"^let\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*:\s*(U8|U16|U32|U64|I8|I16|I32|I64)\s*=\s*([0-9]+)(U8|U16|U32|U64|I8|I16|I32|I64)?;\s*$").unwrap();
    if let Some(caps) = re.captures(input) {
        let name = &caps[1];
        let ty = &caps[2];
        let value = &caps[3];
        let suffix = caps.get(4).map(|m| m.as_str());
        // If a suffix is present, it must match the declared type
        if let Some(sfx) = suffix {
            if sfx != ty {
                return Err("Type suffix does not match declared type");
            }
        }
        let c_type = match ty {
            "U8" => "uint8_t",
            "U16" => "uint16_t",
            "U32" => "uint32_t",
            "U64" => "uint64_t",
            "I8" => "int8_t",
            "I16" => "int16_t",
            "I32" => "int32_t",
            "I64" => "int64_t",
            _ => unreachable!(),
        };
        return Ok(format!("{} {} = {};", c_type, name, value));
    }
    Err("This function only compiles 'let <name> : <type> = <int>[type]?;'")
}

#[cfg(test)]
mod tests {
    use super::*;

    fn assert_compile(input: &str, expected: &str) {
        let result = compile(input);
        assert!(
            result.is_ok(),
            "Expected Ok for input '{}', got {:?}",
            input,
            result
        );
        assert_eq!(result.unwrap(), expected);
    }

    #[test]
    fn test_compile_success() {
        assert_compile("let x : I32 = 0;", "int32_t x = 0;");
    }

    #[test]
    fn test_compile_200() {
        assert_compile("let x : I32 = 200;", "int32_t x = 200;");
    }

    #[test]
    fn test_compile_u8() {
        assert_compile("let a : U8 = 1;", "uint8_t a = 1;");
    }

    #[test]
    fn test_compile_u16() {
        assert_compile("let b : U16 = 2;", "uint16_t b = 2;");
    }

    #[test]
    fn test_compile_u32() {
        assert_compile("let c : U32 = 3;", "uint32_t c = 3;");
    }

    #[test]
    fn test_compile_u64() {
        assert_compile("let d : U64 = 4;", "uint64_t d = 4;");
    }

    #[test]
    fn test_compile_i8() {
        assert_compile("let e : I8 = 5;", "int8_t e = 5;");
    }

    #[test]
    fn test_compile_i16() {
        assert_compile("let f : I16 = 6;", "int16_t f = 6;");
    }

    #[test]
    fn test_compile_i32() {
        assert_compile("let g : I32 = 7;", "int32_t g = 7;");
    }

    #[test]
    fn test_compile_i64() {
        assert_compile("let h : I64 = 8;", "int64_t h = 8;");
    }

    #[test]
    fn test_compile_error() {
        let result = compile("let y : I32 = foo;"); // invalid value
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "This function only compiles 'let <name> : <type> = <int>;'"
        );
    }

    #[test]
    fn test_different_name() {
        let result = compile("let foo : I32 = 0;");
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "int32_t foo = 0;");
    }
}
