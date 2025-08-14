// tests/error_fn_test.rs

#[cfg(test)]
mod tests {
    use magma::always_error;

    #[test]
    fn test_always_error_empty_string() {
        let result = always_error("");
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "");
    }

    #[test]
    fn test_always_error_non_empty_string() {
        let result = always_error("not empty");
        assert!(result.is_err());
        assert_eq!(result.unwrap_err(), "Input was not empty.");
    }
}
