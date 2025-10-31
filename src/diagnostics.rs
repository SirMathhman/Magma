/// Diagnostic error reporting system
use std::fmt;

use crate::token::Span;

/// Diagnostic severity level
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Severity {
    Error,
    Warning,
    Info,
}

impl fmt::Display for Severity {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            Severity::Error => write!(f, "error"),
            Severity::Warning => write!(f, "warning"),
            Severity::Info => write!(f, "info"),
        }
    }
}

/// A compilation diagnostic with source location
#[derive(Debug, Clone)]
pub struct CompilationError {
    pub severity: Severity,
    pub message: String,
    pub span: Span,
    pub source: String,
}

impl CompilationError {
    pub fn error(message: impl Into<String>, span: Span, source: impl Into<String>) -> Self {
        CompilationError {
            severity: Severity::Error,
            message: message.into(),
            span,
            source: source.into(),
        }
    }

    pub fn warning(message: impl Into<String>, span: Span, source: impl Into<String>) -> Self {
        CompilationError {
            severity: Severity::Warning,
            message: message.into(),
            span,
            source: source.into(),
        }
    }

    pub fn format_nice(&self) -> String {
        format!(
            "{}: {} at line {}, column {}\n{}",
            self.severity,
            self.message,
            self.span.line,
            self.span.column,
            self.format_context()
        )
    }

    fn format_context(&self) -> String {
        let lines: Vec<&str> = self.source.lines().collect();
        if self.span.line == 0 || self.span.line > lines.len() {
            return String::new();
        }

        let line_idx = self.span.line - 1;
        let line = lines[line_idx];

        format!(
            "  |\n{:>3} | {}\n  | {}{}\n",
            self.span.line,
            line,
            " ".repeat(self.span.column),
            "^".repeat(self.span.len().max(1))
        )
    }
}

impl fmt::Display for CompilationError {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{}", self.format_nice())
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_error_creation() {
        let span = Span::new(0, 2, 1, 0);
        let error = CompilationError::error("Test error", span, "let x = 1;");
        assert_eq!(error.severity, Severity::Error);
        assert!(error.message.contains("Test error"));
    }

    #[test]
    fn test_error_formatting() {
        let span = Span::new(4, 5, 1, 4);
        let error = CompilationError::error("Invalid token", span, "let x = 1;");
        let formatted = error.format_nice();
        assert!(formatted.contains("Invalid token"));
        assert!(formatted.contains("line 1"));
    }
}
