use magma::lexer::Lexer;
use magma::parser::Parser;
use std::env;
use std::fs;

/// CLI tool for parsing
fn main() {
    let args: Vec<String> = env::args().collect();

    if args.len() < 2 {
        eprintln!("Usage: magma-parse <file.mg>");
        std::process::exit(1);
    }

    let filename = &args[1];

    match fs::read_to_string(filename) {
        Ok(source) => {
            let lexer = Lexer::new(&source);
            match lexer.tokenize() {
                Ok(tokens) => {
                    let parser = Parser::new(tokens);
                    match parser.parse_program() {
                        Ok(program) => {
                            println!("AST:\n{:#?}", program);
                        }
                        Err(errors) => {
                            for error in errors {
                                eprintln!("{}", error);
                            }
                            std::process::exit(1);
                        }
                    }
                }
                Err(errors) => {
                    for error in errors {
                        eprintln!("{}", error);
                    }
                    std::process::exit(1);
                }
            }
        }
        Err(e) => {
            eprintln!("Error reading file '{}': {}", filename, e);
            std::process::exit(1);
        }
    }
}
