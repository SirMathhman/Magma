// tests/error_fn_test.rs

#[cfg(test)]
mod tests {
    use magma::always_error;

    #[test]
    fn test_always_error_returns_err() {
        let result = always_error();
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "This function always returns an error."
        );
    }
}
