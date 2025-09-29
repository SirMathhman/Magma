#[derive(Debug, PartialEq, Clone)]
pub enum TokenKind {
    Identifier(String),
    Number(String),
    Symbol(char),
    Whitespace,
    Comment(String),
    Eof,
}

#[derive(Debug, PartialEq, Clone)]
pub struct Token {
    pub kind: TokenKind,
    pub line: usize,
    pub column: usize,
}

pub struct Lexer<'a> {
    input: &'a str,
    chars: std::str::CharIndices<'a>,
    peek: Option<(usize, char)>,
    line: usize,
    column: usize,
}

impl<'a> Lexer<'a> {
    pub fn new(input: &'a str) -> Self {
        let mut chars = input.char_indices();
        let peek = chars.next();
        Lexer { input, chars, peek, line: 1, column: 1 }
    }

    fn bump(&mut self) -> Option<(usize, char)> {
        let cur = self.peek;
        if let Some((_, ch)) = cur {
            if ch == '\n' {
                self.line += 1;
                self.column = 1;
            } else {
                self.column += 1;
            }
        }
        self.peek = self.chars.next();
        cur
    }

    fn peek_char(&self) -> Option<char> {
        self.peek.map(|(_, c)| c)
    }

    pub fn next_token(&mut self) -> Option<Token> {
        loop {
            let start_line = self.line;
            let start_col = self.column;
            let ch = match self.peek_char() {
                Some(c) => c,
                None => return Some(Token { kind: TokenKind::Eof, line: start_line, column: start_col }),
            };

            if ch.is_whitespace() {
                self.bump();
                continue; // skip whitespace
            }

            // comments start with // to end of line
            if ch == '/' {
                if let Some((_, '/')) = self.peek {
                    // consume '//'
                    self.bump();
                    self.bump();
                    let mut comment = String::new();
                    while let Some(c) = self.peek_char() {
                        if c == '\n' { break; }
                        comment.push(c);
                        self.bump();
                    }
                    return Some(Token { kind: TokenKind::Comment(comment), line: start_line, column: start_col });
                }
            }

            if ch.is_ascii_alphabetic() || ch == '_' {
                let mut id = String::new();
                while let Some(c) = self.peek_char() {
                    if c.is_ascii_alphanumeric() || c == '_' {
                        id.push(c);
                        self.bump();
                    } else { break; }
                }
                return Some(Token { kind: TokenKind::Identifier(id), line: start_line, column: start_col });
            }

            if ch.is_ascii_digit() {
                let mut num = String::new();
                while let Some(c) = self.peek_char() {
                    if c.is_ascii_digit() {
                        num.push(c);
                        self.bump();
                    } else { break; }
                }
                return Some(Token { kind: TokenKind::Number(num), line: start_line, column: start_col });
            }

            // symbols
            self.bump();
            return Some(Token { kind: TokenKind::Symbol(ch), line: start_line, column: start_col });
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn lex_identifiers_and_numbers() {
        let mut lx = Lexer::new("let x = 42;");
        assert_eq!(lx.next_token().unwrap().kind, TokenKind::Identifier("let".into()));
        assert_eq!(lx.next_token().unwrap().kind, TokenKind::Identifier("x".into()));
        assert_eq!(lx.next_token().unwrap().kind, TokenKind::Symbol('='));
        assert_eq!(lx.next_token().unwrap().kind, TokenKind::Number("42".into()));
        assert_eq!(lx.next_token().unwrap().kind, TokenKind::Symbol(';'));
    }

    #[test]
    fn lex_comments() {
        let mut lx = Lexer::new("// hello\n123");
        assert_eq!(lx.next_token().unwrap().kind, TokenKind::Comment(" hello".into()));
        assert_eq!(lx.next_token().unwrap().kind, TokenKind::Number("123".into()));
    }
}
