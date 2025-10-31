/// Driver for the Magma compiler
///
/// Coordinates the compilation pipeline from source code to generated output

use crate::CompilationError;

pub struct Compiler {
    pub source: String,
    pub errors: Vec<CompilationError>,
}

impl Compiler {
    pub fn new(source: impl Into<String>) -> Self {
        Compiler {
            source: source.into(),
            errors: Vec::new(),
        }
    }

    pub fn compile(&mut self) -> Result<String, Vec<CompilationError>> {
        if !self.errors.is_empty() {
            return Err(self.errors.clone());
        }
        Ok(String::new())
    }
}
