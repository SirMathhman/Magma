use magma_compiler::Lexer;
use magma_compiler::lexer::TokenKind;

#[test]
fn run_cli_example() {
    let input = "foo 123 //a comment\n;";
    let mut lx = Lexer::new(input);
    let mut kinds = Vec::new();
    while let Some(tok) = lx.next_token() {
        if let TokenKind::Eof = &tok.kind { 
            kinds.push(tok.kind);
            break; 
        }
        kinds.push(tok.kind);
    }
    assert!(kinds.iter().any(|k| matches!(k, TokenKind::Identifier(s) if s == "foo")));
}
