pub fn always_error() -> Result<(), &'static str> {
    Err("This function always returns an error.")
}
