/// Compiles a simple assignment from a custom syntax to C syntax.
/// Specifically, translates 'let x : I32 = 0;' to 'int32_t x = 0;'.
pub fn compile(input: &str) -> Result<String, &'static str> {
    let input = input.trim();
    // Updated regex: support Bool type, true/false, and char literals for U8
    let re = regex::Regex::new(r"^let\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*:\s*(U8|U16|U32|U64|I8|I16|I32|I64|Bool)\s*=\s*(-?[0-9]+(U8|U16|U32|U64|I8|I16|I32|I64)?|true|false|'[^']');\s*$").unwrap();
    if let Some(caps) = re.captures(input) {
        let name = &caps[1];
        let ty = &caps[2];
        let value = &caps[3];
        // Handle Bool type
        if ty == "Bool" {
            if value == "true" || value == "false" {
                return Ok(format!("bool {} = {};", name, value));
            } else {
                return Err("Bool type must be assigned true or false");
            }
        }
        // Handle char literal for U8
        if ty == "U8" && value.starts_with("'") && value.ends_with("'") && value.len() == 3 {
            let c = value.chars().nth(1).unwrap();
            let ascii = c as u8;
            return Ok(format!("uint8_t {} = {};", name, ascii));
        }
        // Handle integer types (including negative for signed)
        let suffix_re = regex::Regex::new(r"^(-?[0-9]+)(U8|U16|U32|U64|I8|I16|I32|I64)?$").unwrap();
        if let Some(val_caps) = suffix_re.captures(value) {
            let val = &val_caps[1];
            let suffix = val_caps.get(2).map(|m| m.as_str());
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
            // Bounds checking
            let in_bounds = match ty {
                "U8" => val.parse::<u64>().map_or(false, |v| v <= 255),
                "U16" => val.parse::<u64>().map_or(false, |v| v <= 65535),
                "U32" => val.parse::<u64>().map_or(false, |v| v <= 4294967295),
                "U64" => val
                    .parse::<u128>()
                    .map_or(false, |v| v <= 18446744073709551615),
                "I8" => val == "-128" || val.parse::<i8>().is_ok(),
                "I16" => val == "-32768" || val.parse::<i16>().is_ok(),
                "I32" => val == "-2147483648" || val.parse::<i32>().is_ok(),
                "I64" => val == "-9223372036854775808" || val.parse::<i64>().is_ok(),
                _ => false,
            };
            if !in_bounds {
                return Err(Box::leak(
                    format!("Invalid input: {}", input).into_boxed_str(),
                ));
            }
            return Ok(format!("{} {} = {};", c_type, name, val));
        }
    }
    Err(Box::leak(
        format!("Invalid input: {}", input).into_boxed_str(),
    ))
}

#[cfg(test)]
mod tests {
    #[test]
    fn test_bounds_u8() {
        assert_compile("let a : U8 = 0;", "uint8_t a = 0;");
        assert_compile("let b : U8 = 255;", "uint8_t b = 255;");
        let result = compile("let c : U8 = 256;");
        assert!(result.is_err());
    }

    #[test]
    fn test_bounds_u16() {
        assert_compile("let a : U16 = 0;", "uint16_t a = 0;");
        assert_compile("let b : U16 = 65535;", "uint16_t b = 65535;");
        let result = compile("let c : U16 = 65536;");
        assert!(result.is_err());
    }

    #[test]
    fn test_bounds_u32() {
        assert_compile("let a : U32 = 0;", "uint32_t a = 0;");
        assert_compile("let b : U32 = 4294967295;", "uint32_t b = 4294967295;");
        let result = compile("let c : U32 = 4294967296;");
        assert!(result.is_err());
    }

    #[test]
    fn test_bounds_u64() {
        assert_compile("let a : U64 = 0;", "uint64_t a = 0;");
        assert_compile(
            "let b : U64 = 18446744073709551615;",
            "uint64_t b = 18446744073709551615;",
        );
        let result = compile("let c : U64 = 18446744073709551616;");
        assert!(result.is_err());
    }

    #[test]
    fn test_bounds_i8() {
        assert_compile("let a : I8 = -128;", "int8_t a = -128;");
        assert_compile("let b : I8 = 127;", "int8_t b = 127;");
        let result = compile("let c : I8 = -129;");
        assert!(result.is_err());
        let result = compile("let d : I8 = 128;");
        assert!(result.is_err());
    }

    #[test]
    fn test_bounds_i16() {
        assert_compile("let a : I16 = -32768;", "int16_t a = -32768;");
        assert_compile("let b : I16 = 32767;", "int16_t b = 32767;");
        let result = compile("let c : I16 = -32769;");
        assert!(result.is_err());
        let result = compile("let d : I16 = 32768;");
        assert!(result.is_err());
    }

    #[test]
    fn test_bounds_i32() {
        assert_compile("let a : I32 = -2147483648;", "int32_t a = -2147483648;");
        assert_compile("let b : I32 = 2147483647;", "int32_t b = 2147483647;");
        let result = compile("let c : I32 = -2147483649;");
        assert!(result.is_err());
        let result = compile("let d : I32 = 2147483648;");
        assert!(result.is_err());
    }

    #[test]
    fn test_bounds_i64() {
        assert_compile(
            "let a : I64 = -9223372036854775808;",
            "int64_t a = -9223372036854775808;",
        );
        assert_compile(
            "let b : I64 = 9223372036854775807;",
            "int64_t b = 9223372036854775807;",
        );
        let result = compile("let c : I64 = -9223372036854775809;");
        assert!(result.is_err());
        let result = compile("let d : I64 = 9223372036854775808;");
        assert!(result.is_err());
    }
    #[test]
    fn test_compile_u8_char() {
        assert_compile("let x : U8 = 'a';", "uint8_t x = 97;");
    }
    #[test]
    fn test_compile_bool_true() {
        assert_compile("let value : Bool = true;", "bool value = true;");
    }

    #[test]
    fn test_compile_bool_false() {
        assert_compile("let value : Bool = false;", "bool value = false;");
    }
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
    fn test_compile_with_suffix() {
        assert_compile("let x : I32 = 0I32;", "int32_t x = 0;");
    }

    #[test]
    fn test_compile_invalid_suffix() {
        let result = compile("let x : I32 = 0U64;");
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "Type suffix does not match declared type"
        );
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
        let input = "let y : I32 = foo;";
        let result = compile(input); // invalid value
        assert!(result.is_err());
        assert_eq!(result.unwrap_err(), &format!("Invalid input: {}", input));
    }

    #[test]
    fn test_different_name() {
        let result = compile("let foo : I32 = 0;");
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "int32_t foo = 0;");
    }
}
