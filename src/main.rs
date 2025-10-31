use magma::lexer::Lexer;
use std::env;
use std::fs;

fn main() {
    let args: Vec<String> = env::args().collect();

    if args.len() < 2 {
        eprintln!("Usage: magma <command> [file]");
        eprintln!("Commands: lex, parse");
        std::process::exit(1);
    }

    let command = &args[1];
    let file = args.get(2).map(|s| s.as_str());

    match command.as_str() {
        "lex" => {
            if let Some(filename) = file {
                match fs::read_to_string(filename) {
                    Ok(source) => {
                        let lexer = Lexer::new(&source);
                        match lexer.tokenize() {
                            Ok(tokens) => {
                                for token in tokens {
                                    println!("{}", token);
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
                        eprintln!("Error reading file: {}", e);
                        std::process::exit(1);
                    }
                }
            } else {
                eprintln!("Usage: magma lex <file>");
                std::process::exit(1);
            }
        }
        _ => {
            eprintln!("Unknown command: {}", command);
            std::process::exit(1);
        }
    }
}
