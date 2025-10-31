pub mod ast;
/// Magma Compiler - Core Library
///
/// Main compilation pipeline:
/// Source Code → [Lexer] → Tokens → [Parser] → AST → [Semantic Analysis] → HIR
/// → [Codegen] → (TypeScript | JavaScript | LLVM IR)
pub mod diagnostics;
pub mod driver;
pub mod lexer;
pub mod parser;
pub mod semantic;
pub mod token;

pub use diagnostics::CompilationError;
pub use token::{Span, Token, TokenKind};

pub fn version() -> &'static str {
    "0.1.0"
}
