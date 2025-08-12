/// A simple function that always returns an error.
pub fn always_error() -> Result<(), &'static str> {
    Err("This function always fails")
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_always_error() {
        let result = always_error();
        assert!(result.is_err());
        assert_eq!(result.unwrap_err(), "This function always fails");
    }
}
