/// Returns Ok with the input string if it is empty, otherwise always returns an error.
pub fn compile(input: &str) -> Result<&str, &'static str> {
    if input.is_empty() {
        Ok("")
    } else {
        Err("This function always fails")
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_always_error() {
        let result = compile("not empty");
        assert!(result.is_err());
        assert_eq!(result.unwrap_err(), "This function always fails");
    }

    #[test]
    fn test_empty_string() {
        let result = compile("");
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "");
    }
}
