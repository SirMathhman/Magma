/// Lexer for the Magma language
///
/// Tokenizes Magma source code into a stream of tokens for parsing.
/// Week 1 Implementation

use crate::token::{Token, TokenKind, Span};
use crate::diagnostics::CompilationError;

pub struct Lexer {
    input: Vec<char>,
    position: usize,
    line: usize,
    column: usize,
    start_pos: usize,
    start_line: usize,
    start_column: usize,
}

impl Lexer {
    pub fn new(input: &str) -> Self {
        Lexer {
            input: input.chars().collect(),
            position: 0,
            line: 1,
            column: 1,
            start_pos: 0,
            start_line: 1,
            start_column: 1,
        }
    }

    /// Tokenize entire input and return token stream
    pub fn tokenize(mut self) -> Result<Vec<Token>, Vec<CompilationError>> {
        let mut tokens = Vec::new();
        let mut errors = Vec::new();

        loop {
            match self.next_token() {
                Ok(token) => {
                    let is_eof = token.kind == TokenKind::Eof;
                    tokens.push(token);
                    if is_eof {
                        break;
                    }
                }
                Err(e) => errors.push(e),
            }
        }

        if !errors.is_empty() {
            return Err(errors);
        }

        Ok(tokens)
    }

    /// Get next token from input
    fn next_token(&mut self) -> Result<Token, CompilationError> {
        self.skip_whitespace_and_comments();

        if self.position >= self.input.len() {
            return Ok(Token::eof(self.current_span()));
        }

        self.mark_token_start();
        let ch = self.current();

        match ch {
            // Single-character tokens
            '(' => {
                self.advance();
                Ok(Token::new(TokenKind::LeftParen, self.token_span()))
            }
            ')' => {
                self.advance();
                Ok(Token::new(TokenKind::RightParen, self.token_span()))
            }
            '{' => {
                self.advance();
                Ok(Token::new(TokenKind::LeftBrace, self.token_span()))
            }
            '}' => {
                self.advance();
                Ok(Token::new(TokenKind::RightBrace, self.token_span()))
            }
            '[' => {
                self.advance();
                Ok(Token::new(TokenKind::LeftBracket, self.token_span()))
            }
            ']' => {
                self.advance();
                Ok(Token::new(TokenKind::RightBracket, self.token_span()))
            }
            ';' => {
                self.advance();
                Ok(Token::new(TokenKind::Semicolon, self.token_span()))
            }
            ',' => {
                self.advance();
                Ok(Token::new(TokenKind::Comma, self.token_span()))
            }
            '.' => {
                self.advance();
                Ok(Token::new(TokenKind::Dot, self.token_span()))
            }
            ':' => {
                self.advance();
                Ok(Token::new(TokenKind::Colon, self.token_span()))
            }
            '?' => {
                self.advance();
                Ok(Token::new(TokenKind::Question, self.token_span()))
            }

            // Multi-character operators
            '+' => {
                self.advance();
                Ok(Token::new(TokenKind::Plus, self.token_span()))
            }
            '%' => {
                self.advance();
                Ok(Token::new(TokenKind::Percent, self.token_span()))
            }
            '^' => {
                self.advance();
                Ok(Token::new(TokenKind::Caret, self.token_span()))
            }

            '-' => {
                self.advance();
                Ok(Token::new(TokenKind::Minus, self.token_span()))
            }

            '*' => {
                self.advance();
                Ok(Token::new(TokenKind::Star, self.token_span()))
            }

            '/' => {
                self.advance();
                Ok(Token::new(TokenKind::Slash, self.token_span()))
            }

            '=' => {
                self.advance();
                if self.current() == '=' {
                    self.advance();
                    Ok(Token::new(TokenKind::EqualEqual, self.token_span()))
                } else if self.current() == '>' {
                    self.advance();
                    Ok(Token::new(TokenKind::Arrow, self.token_span()))
                } else {
                    Ok(Token::new(TokenKind::Equal, self.token_span()))
                }
            }

            '!' => {
                self.advance();
                if self.current() == '=' {
                    self.advance();
                    Ok(Token::new(TokenKind::BangEqual, self.token_span()))
                } else {
                    Ok(Token::new(TokenKind::Bang, self.token_span()))
                }
            }

            '<' => {
                self.advance();
                if self.current() == '=' {
                    self.advance();
                    Ok(Token::new(TokenKind::LessEqual, self.token_span()))
                } else if self.current() == '<' {
                    self.advance();
                    Ok(Token::new(TokenKind::LeftShift, self.token_span()))
                } else {
                    Ok(Token::new(TokenKind::Less, self.token_span()))
                }
            }

            '>' => {
                self.advance();
                if self.current() == '=' {
                    self.advance();
                    Ok(Token::new(TokenKind::GreaterEqual, self.token_span()))
                } else if self.current() == '>' {
                    self.advance();
                    Ok(Token::new(TokenKind::RightShift, self.token_span()))
                } else {
                    Ok(Token::new(TokenKind::Greater, self.token_span()))
                }
            }

            '&' => {
                self.advance();
                if self.current() == '&' {
                    self.advance();
                    Ok(Token::new(TokenKind::AmpersandAmpersand, self.token_span()))
                } else {
                    Ok(Token::new(TokenKind::Ampersand, self.token_span()))
                }
            }

            '|' => {
                self.advance();
                if self.current() == '|' {
                    self.advance();
                    Ok(Token::new(TokenKind::PipePipe, self.token_span()))
                } else {
                    Ok(Token::new(TokenKind::Pipe, self.token_span()))
                }
            }

            // String literals
            '"' => self.lex_string(),

            // Identifiers and keywords
            _ if ch.is_alphabetic() || ch == '_' => self.lex_identifier(),

            // Numbers
            _ if ch.is_ascii_digit() => self.lex_number(),

            _ => {
                self.advance();
                Err(CompilationError::error(
                    format!("Unexpected character '{}'", ch),
                    self.token_span(),
                    "",
                ))
            }
        }
    }

    fn lex_string(&mut self) -> Result<Token, CompilationError> {
        self.advance(); // consume opening quote

        let mut value = String::new();
        while self.position < self.input.len() && self.current() != '"' {
            if self.current() == '\\' {
                self.advance();
                if self.position >= self.input.len() {
                    return Err(CompilationError::error(
                        "Unterminated string escape",
                        self.token_span(),
                        "",
                    ));
                }
                match self.current() {
                    'n' => value.push('\n'),
                    't' => value.push('\t'),
                    'r' => value.push('\r'),
                    '\\' => value.push('\\'),
                    '"' => value.push('"'),
                    _ => {
                        value.push('\\');
                        value.push(self.current());
                    }
                }
                self.advance();
            } else {
                value.push(self.current());
                self.advance();
            }
        }

        if self.position >= self.input.len() {
            return Err(CompilationError::error(
                "Unterminated string literal",
                self.token_span(),
                "",
            ));
        }

        self.advance(); // consume closing quote
        Ok(Token::new(TokenKind::StringLiteral(value), self.token_span()))
    }

    fn lex_identifier(&mut self) -> Result<Token, CompilationError> {
        let start = self.position;

        while self.position < self.input.len()
            && (self.current().is_alphanumeric() || self.current() == '_')
        {
            self.advance();
        }

        let ident: String = self.input[start..self.position].iter().collect();

        let kind = match ident.as_str() {
            "fn" => TokenKind::Fn,
            "struct" => TokenKind::Struct,
            "class" => TokenKind::Class,
            "let" => TokenKind::Let,
            "mut" => TokenKind::Mut,
            "if" => TokenKind::If,
            "else" => TokenKind::Else,
            "match" => TokenKind::Match,
            "while" => TokenKind::While,
            "for" => TokenKind::For,
            "in" => TokenKind::In,
            "break" => TokenKind::Break,
            "continue" => TokenKind::Continue,
            "return" => TokenKind::Return,
            "priv" => TokenKind::Priv,
            "Bool" => TokenKind::Bool,
            "I32" => TokenKind::I32,
            "I64" => TokenKind::I64,
            "F32" => TokenKind::F32,
            "F64" => TokenKind::F64,
            "String" => TokenKind::String,
            "Void" => TokenKind::Void,
            "true" => TokenKind::True,
            "false" => TokenKind::False,
            _ => TokenKind::Identifier(ident),
        };

        Ok(Token::new(kind, self.token_span()))
    }

    fn lex_number(&mut self) -> Result<Token, CompilationError> {
        let start = self.position;

        while self.position < self.input.len() && self.current().is_ascii_digit() {
            self.advance();
        }

        // Check for float
        if self.position < self.input.len() && self.current() == '.' {
            let next_pos = self.position + 1;
            if next_pos < self.input.len() && self.input[next_pos].is_ascii_digit() {
                self.advance(); // consume dot
                while self.position < self.input.len() && self.current().is_ascii_digit() {
                    self.advance();
                }

                let num_str: String = self.input[start..self.position].iter().collect();
                let value = num_str.parse::<f64>().map_err(|_| {
                    CompilationError::error(
                        format!("Invalid float literal: {}", num_str),
                        self.token_span(),
                        "",
                    )
                })?;

                return Ok(Token::new(TokenKind::Float(value), self.token_span()));
            }
        }

        let num_str: String = self.input[start..self.position].iter().collect();
        let value = num_str.parse::<i64>().map_err(|_| {
            CompilationError::error(
                format!("Invalid integer literal: {}", num_str),
                self.token_span(),
                "",
            )
        })?;

        Ok(Token::new(TokenKind::Integer(value), self.token_span()))
    }

    fn skip_whitespace_and_comments(&mut self) {
        loop {
            if self.position >= self.input.len() {
                break;
            }

            match self.current() {
                // Whitespace
                ' ' | '\t' | '\r' => self.advance(),

                // Newline
                '\n' => {
                    self.line += 1;
                    self.column = 1;
                    self.position += 1;
                }

                // Single-line comment
                '/' if self.peek() == Some('/') => {
                    self.advance();
                    self.advance();
                    while self.position < self.input.len() && self.current() != '\n' {
                        self.advance();
                    }
                }

                // Multi-line comment
                '/' if self.peek() == Some('*') => {
                    self.advance();
                    self.advance();
                    while self.position < self.input.len() {
                        if self.current() == '*' && self.peek() == Some('/') {
                            self.advance();
                            self.advance();
                            break;
                        }
                        if self.current() == '\n' {
                            self.line += 1;
                            self.column = 0;
                        }
                        self.advance();
                    }
                }

                _ => break,
            }
        }
    }

    fn mark_token_start(&mut self) {
        self.start_pos = self.position;
        self.start_line = self.line;
        self.start_column = self.column;
    }

    fn token_span(&self) -> Span {
        Span::new(self.start_pos, self.position, self.start_line, self.start_column)
    }

    fn current_span(&self) -> Span {
        Span::new(self.position, self.position, self.line, self.column)
    }

    fn current(&self) -> char {
        if self.position >= self.input.len() {
            '\0'
        } else {
            self.input[self.position]
        }
    }

    fn peek(&self) -> Option<char> {
        if self.position + 1 < self.input.len() {
            Some(self.input[self.position + 1])
        } else {
            None
        }
    }

    fn advance(&mut self) {
        if self.position < self.input.len() {
            self.position += 1;
            self.column += 1;
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_empty_input() {
        let lexer = Lexer::new("");
        let tokens = lexer.tokenize().unwrap();
        assert_eq!(tokens.len(), 1);
        assert_eq!(tokens[0].kind, TokenKind::Eof);
    }

    #[test]
    fn test_single_char_tokens() {
        let input = "( ) { } [ ] ; , . : ?";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        let expected = vec![
            TokenKind::LeftParen,
            TokenKind::RightParen,
            TokenKind::LeftBrace,
            TokenKind::RightBrace,
            TokenKind::LeftBracket,
            TokenKind::RightBracket,
            TokenKind::Semicolon,
            TokenKind::Comma,
            TokenKind::Dot,
            TokenKind::Colon,
            TokenKind::Question,
            TokenKind::Eof,
        ];

        assert_eq!(tokens.len(), expected.len());
        for (token, expected_kind) in tokens.iter().zip(expected.iter()) {
            assert_eq!(&token.kind, expected_kind);
        }
    }

    #[test]
    fn test_operators() {
        let input = "+ - * / % = == != < <= > >= ! & | ^ << >> && ||";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        let expected = vec![
            TokenKind::Plus,
            TokenKind::Minus,
            TokenKind::Star,
            TokenKind::Slash,
            TokenKind::Percent,
            TokenKind::Equal,
            TokenKind::EqualEqual,
            TokenKind::BangEqual,
            TokenKind::Less,
            TokenKind::LessEqual,
            TokenKind::Greater,
            TokenKind::GreaterEqual,
            TokenKind::Bang,
            TokenKind::Ampersand,
            TokenKind::Pipe,
            TokenKind::Caret,
            TokenKind::LeftShift,
            TokenKind::RightShift,
            TokenKind::AmpersandAmpersand,
            TokenKind::PipePipe,
            TokenKind::Eof,
        ];

        assert_eq!(tokens.len(), expected.len());
        for (token, expected_kind) in tokens.iter().zip(expected.iter()) {
            assert_eq!(&token.kind, expected_kind);
        }
    }

    #[test]
    fn test_arrow_operator() {
        let input = "=>";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();
        assert_eq!(tokens[0].kind, TokenKind::Arrow);
    }

    #[test]
    fn test_keywords() {
        let input = "fn struct class let mut if else match while for in break continue return priv";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        let expected = vec![
            TokenKind::Fn,
            TokenKind::Struct,
            TokenKind::Class,
            TokenKind::Let,
            TokenKind::Mut,
            TokenKind::If,
            TokenKind::Else,
            TokenKind::Match,
            TokenKind::While,
            TokenKind::For,
            TokenKind::In,
            TokenKind::Break,
            TokenKind::Continue,
            TokenKind::Return,
            TokenKind::Priv,
            TokenKind::Eof,
        ];

        assert_eq!(tokens.len(), expected.len());
        for (token, expected_kind) in tokens.iter().zip(expected.iter()) {
            assert_eq!(&token.kind, expected_kind);
        }
    }

    #[test]
    fn test_type_keywords() {
        let input = "Bool I32 I64 F32 F64 String Void";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        let expected = vec![
            TokenKind::Bool,
            TokenKind::I32,
            TokenKind::I64,
            TokenKind::F32,
            TokenKind::F64,
            TokenKind::String,
            TokenKind::Void,
            TokenKind::Eof,
        ];

        assert_eq!(tokens.len(), expected.len());
        for (token, expected_kind) in tokens.iter().zip(expected.iter()) {
            assert_eq!(&token.kind, expected_kind);
        }
    }

    #[test]
    fn test_boolean_literals() {
        let input = "true false";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        assert_eq!(tokens[0].kind, TokenKind::True);
        assert_eq!(tokens[1].kind, TokenKind::False);
    }

    #[test]
    fn test_identifiers() {
        let input = "x foo_bar _private MyClass";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        match &tokens[0].kind {
            TokenKind::Identifier(s) => assert_eq!(s, "x"),
            _ => panic!("Expected identifier"),
        }
        match &tokens[1].kind {
            TokenKind::Identifier(s) => assert_eq!(s, "foo_bar"),
            _ => panic!("Expected identifier"),
        }
        match &tokens[2].kind {
            TokenKind::Identifier(s) => assert_eq!(s, "_private"),
            _ => panic!("Expected identifier"),
        }
        match &tokens[3].kind {
            TokenKind::Identifier(s) => assert_eq!(s, "MyClass"),
            _ => panic!("Expected identifier"),
        }
    }

    #[test]
    fn test_integer_literals() {
        let input = "0 42 1000 999";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        assert_eq!(tokens[0].kind, TokenKind::Integer(0));
        assert_eq!(tokens[1].kind, TokenKind::Integer(42));
        assert_eq!(tokens[2].kind, TokenKind::Integer(1000));
        assert_eq!(tokens[3].kind, TokenKind::Integer(999));
    }

    #[test]
    fn test_float_literals() {
        let input = "3.14 0.5 1.0";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        assert_eq!(tokens[0].kind, TokenKind::Float(3.14));
        assert_eq!(tokens[1].kind, TokenKind::Float(0.5));
        assert_eq!(tokens[2].kind, TokenKind::Float(1.0));
    }

    #[test]
    fn test_string_literals() {
        let input = r#""hello" "world with spaces""#;
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        match &tokens[0].kind {
            TokenKind::StringLiteral(s) => assert_eq!(s, "hello"),
            _ => panic!("Expected string literal"),
        }
        match &tokens[1].kind {
            TokenKind::StringLiteral(s) => assert_eq!(s, "world with spaces"),
            _ => panic!("Expected string literal"),
        }
    }

    #[test]
    fn test_string_escapes() {
        let input = r#""hello\nworld\ttab""#;
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        match &tokens[0].kind {
            TokenKind::StringLiteral(s) => {
                assert!(s.contains('\n'));
                assert!(s.contains('\t'));
            }
            _ => panic!("Expected string literal"),
        }
    }

    #[test]
    fn test_single_line_comments() {
        let input = "let x = 5; // this is a comment\nlet y = 10;";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        // Should skip the comment and parse the second let
        assert_eq!(tokens[0].kind, TokenKind::Let);
        // After: let, x, =, 5, ;, we get the second "let"
        assert_eq!(tokens[5].kind, TokenKind::Let);
    }

    #[test]
    fn test_multi_line_comments() {
        let input = "let x = 5; /* multi\nline\ncomment */ let y = 10;";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        assert_eq!(tokens[0].kind, TokenKind::Let);
        // After: let, x, =, 5, ;, we get the second "let"
        assert_eq!(tokens[5].kind, TokenKind::Let);
    }

    #[test]
    fn test_simple_function() {
        let input = "fn add(a : I32, b : I32) : I32 => a + b;";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        assert_eq!(tokens[0].kind, TokenKind::Fn);
        match &tokens[1].kind {
            TokenKind::Identifier(s) => assert_eq!(s, "add"),
            _ => panic!("Expected identifier"),
        }
        assert_eq!(tokens[2].kind, TokenKind::LeftParen);
    }

    #[test]
    fn test_whitespace_handling() {
        let input = "   let   x   =   5   ;   ";
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().unwrap();

        assert_eq!(tokens[0].kind, TokenKind::Let);
        match &tokens[1].kind {
            TokenKind::Identifier(s) => assert_eq!(s, "x"),
            _ => panic!("Expected identifier"),
        }
        assert_eq!(tokens[2].kind, TokenKind::Equal);
        assert_eq!(tokens[3].kind, TokenKind::Integer(5));
        assert_eq!(tokens[4].kind, TokenKind::Semicolon);
    }
}
