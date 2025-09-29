use std::io::{self, Read};
use magma_compiler::Lexer;

fn main() {
    let mut input = String::new();
    io::stdin().read_to_string(&mut input).expect("failed to read stdin");
    let mut lexer = Lexer::new(&input);
    while let Some(tok) = lexer.next_token() {
        println!("{:?}", tok);
    }
}
