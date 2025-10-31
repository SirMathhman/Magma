/// Token definitions for the Magma language lexer
use std::fmt;

/// Source location for error reporting
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub struct Span {
    pub start: usize,
    pub end: usize,
    pub line: usize,
    pub column: usize,
}

impl Span {
    pub fn new(start: usize, end: usize, line: usize, column: usize) -> Self {
        Span {
            start,
            end,
            line,
            column,
        }
    }

    pub fn len(&self) -> usize {
        self.end - self.start
    }

    pub fn merge(a: Span, b: Span) -> Span {
        Span {
            start: a.start.min(b.start),
            end: a.end.max(b.end),
            line: a.line,
            column: a.column,
        }
    }
}

/// Token type enumeration
#[derive(Debug, Clone, PartialEq)]
pub enum TokenKind {
    // Keywords
    Fn,
    Struct,
    Class,
    Let,
    Mut,
    If,
    Else,
    Match,
    While,
    For,
    In,
    Break,
    Continue,
    Return,
    Priv,

    // Type keywords
    Bool,
    I32,
    I64,
    F32,
    F64,
    String,
    Void,

    // Identifiers and literals
    Identifier(String),
    Integer(i64),
    Float(f64),
    StringLiteral(String),
    True,
    False,

    // Operators
    Plus,
    Minus,
    Star,
    Slash,
    Percent,
    Equal,
    EqualEqual,
    BangEqual,
    Less,
    LessEqual,
    Greater,
    GreaterEqual,
    Bang,
    Ampersand,
    Pipe,
    Caret,
    LeftShift,
    RightShift,
    AmpersandAmpersand,
    PipePipe,
    Arrow,    // =>
    Question, // ?
    Colon,
    Semicolon,
    Comma,
    Dot,

    // Delimiters
    LeftParen,
    RightParen,
    LeftBrace,
    RightBrace,
    LeftBracket,
    RightBracket,
    LeftAngle,
    RightAngle,

    // Special
    Eof,
}

impl fmt::Display for TokenKind {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            TokenKind::Fn => write!(f, "fn"),
            TokenKind::Struct => write!(f, "struct"),
            TokenKind::Class => write!(f, "class"),
            TokenKind::Let => write!(f, "let"),
            TokenKind::Mut => write!(f, "mut"),
            TokenKind::If => write!(f, "if"),
            TokenKind::Else => write!(f, "else"),
            TokenKind::Match => write!(f, "match"),
            TokenKind::While => write!(f, "while"),
            TokenKind::For => write!(f, "for"),
            TokenKind::In => write!(f, "in"),
            TokenKind::Break => write!(f, "break"),
            TokenKind::Continue => write!(f, "continue"),
            TokenKind::Return => write!(f, "return"),
            TokenKind::Priv => write!(f, "priv"),
            TokenKind::Bool => write!(f, "Bool"),
            TokenKind::I32 => write!(f, "I32"),
            TokenKind::I64 => write!(f, "I64"),
            TokenKind::F32 => write!(f, "F32"),
            TokenKind::F64 => write!(f, "F64"),
            TokenKind::String => write!(f, "String"),
            TokenKind::Void => write!(f, "Void"),
            TokenKind::Identifier(s) => write!(f, "{}", s),
            TokenKind::Integer(n) => write!(f, "{}", n),
            TokenKind::Float(n) => write!(f, "{}", n),
            TokenKind::StringLiteral(s) => write!(f, "\"{}\"", s),
            TokenKind::True => write!(f, "true"),
            TokenKind::False => write!(f, "false"),
            TokenKind::Plus => write!(f, "+"),
            TokenKind::Minus => write!(f, "-"),
            TokenKind::Star => write!(f, "*"),
            TokenKind::Slash => write!(f, "/"),
            TokenKind::Percent => write!(f, "%"),
            TokenKind::Equal => write!(f, "="),
            TokenKind::EqualEqual => write!(f, "=="),
            TokenKind::BangEqual => write!(f, "!="),
            TokenKind::Less => write!(f, "<"),
            TokenKind::LessEqual => write!(f, "<="),
            TokenKind::Greater => write!(f, ">"),
            TokenKind::GreaterEqual => write!(f, ">="),
            TokenKind::Bang => write!(f, "!"),
            TokenKind::Ampersand => write!(f, "&"),
            TokenKind::Pipe => write!(f, "|"),
            TokenKind::Caret => write!(f, "^"),
            TokenKind::LeftShift => write!(f, "<<"),
            TokenKind::RightShift => write!(f, ">>"),
            TokenKind::AmpersandAmpersand => write!(f, "&&"),
            TokenKind::PipePipe => write!(f, "||"),
            TokenKind::Arrow => write!(f, "=>"),
            TokenKind::Question => write!(f, "?"),
            TokenKind::Colon => write!(f, ":"),
            TokenKind::Semicolon => write!(f, ";"),
            TokenKind::Comma => write!(f, ","),
            TokenKind::Dot => write!(f, "."),
            TokenKind::LeftParen => write!(f, "("),
            TokenKind::RightParen => write!(f, ")"),
            TokenKind::LeftBrace => write!(f, "{{"),
            TokenKind::RightBrace => write!(f, "}}"),
            TokenKind::LeftBracket => write!(f, "["),
            TokenKind::RightBracket => write!(f, "]"),
            TokenKind::LeftAngle => write!(f, "<"),
            TokenKind::RightAngle => write!(f, ">"),
            TokenKind::Eof => write!(f, "EOF"),
        }
    }
}

/// A token with location information
#[derive(Debug, Clone)]
pub struct Token {
    pub kind: TokenKind,
    pub span: Span,
}

impl Token {
    pub fn new(kind: TokenKind, span: Span) -> Self {
        Token { kind, span }
    }

    pub fn eof(span: Span) -> Self {
        Token {
            kind: TokenKind::Eof,
            span,
        }
    }
}

impl fmt::Display for Token {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(
            f,
            "{} at line {} col {}",
            self.kind, self.span.line, self.span.column
        )
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_span_creation() {
        let span = Span::new(0, 5, 1, 0);
        assert_eq!(span.len(), 5);
    }

    #[test]
    fn test_span_merge() {
        let span1 = Span::new(0, 5, 1, 0);
        let span2 = Span::new(5, 10, 1, 5);
        let merged = Span::merge(span1, span2);
        assert_eq!(merged.start, 0);
        assert_eq!(merged.end, 10);
    }

    #[test]
    fn test_token_display() {
        let span = Span::new(0, 2, 1, 0);
        let token = Token::new(TokenKind::Fn, span);
        assert!(token.to_string().contains("fn"));
    }
}
