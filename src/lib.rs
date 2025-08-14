pub fn always_error(input: &str) -> Result<String, &'static str> {
    if input.is_empty() {
        Ok(String::new())
    } else {
        Err("Input was not empty.")
    }
}
