use magma::driver::{CompilationRequest, CompilationTarget, MagmaDriver};
use std::env;
use std::fs;
use std::path::PathBuf;

fn main() {
    let args: Vec<String> = env::args().collect();

    if args.len() < 2 {
        print_usage();
        std::process::exit(1);
    }

    let command = &args[1];

    match command.as_str() {
        "compile" => compile_command(&args),
        "lex" => lex_command(&args),
        "parse" => parse_command(&args),
        "--help" | "-h" | "help" => print_help(),
        "--version" | "-v" => println!("Magma {}", magma::version()),
        _ => {
            eprintln!("Unknown command: {}", command);
            print_usage();
            std::process::exit(1);
        }
    }
}

fn compile_command(args: &[String]) {
    if args.len() < 3 {
        eprintln!("Usage: magma compile [--target ts|js|llvm] [--output FILE] <input>");
        std::process::exit(1);
    }

    let mut target = CompilationTarget::TypeScript;
    let mut output_path: Option<PathBuf> = None;
    let mut input_file: Option<&str> = None;

    let mut i = 2;
    while i < args.len() {
        match args[i].as_str() {
            "--target" => {
                if i + 1 >= args.len() {
                    eprintln!("Error: --target requires an argument");
                    std::process::exit(1);
                }
                i += 1;
                if let Some(t) = CompilationTarget::from_string(&args[i]) {
                    target = t;
                } else {
                    eprintln!(
                        "Error: invalid target '{}'. Must be 'ts', 'js', or 'llvm'",
                        args[i]
                    );
                    std::process::exit(1);
                }
            }
            "--output" | "-o" => {
                if i + 1 >= args.len() {
                    eprintln!("Error: --output requires an argument");
                    std::process::exit(1);
                }
                i += 1;
                output_path = Some(PathBuf::from(&args[i]));
            }
            _ => {
                if input_file.is_none() && !args[i].starts_with("--") {
                    input_file = Some(&args[i]);
                }
            }
        }
        i += 1;
    }

    let input_file = match input_file {
        Some(f) => f,
        None => {
            eprintln!("Error: no input file specified");
            std::process::exit(1);
        }
    };

    // Read source file
    let source = match fs::read_to_string(input_file) {
        Ok(s) => s,
        Err(e) => {
            eprintln!("Error reading file '{}': {}", input_file, e);
            std::process::exit(1);
        }
    };

    // Set default output path if not specified
    let output_path = output_path.or_else(|| {
        let path = PathBuf::from(input_file);
        let stem = path.file_stem()?;
        Some(
            path.parent()
                .unwrap_or_else(|| std::path::Path::new("."))
                .join(format!("{}.{}", stem.to_string_lossy(), target.extension())),
        )
    });

    // Compile
    let request = CompilationRequest {
        source,
        target,
        output_path: output_path.clone(),
    };

    match MagmaDriver::compile(request) {
        Ok(result) => {
            if let Some(path) = output_path {
                println!(
                    "✓ Successfully compiled {} to {}",
                    input_file,
                    path.display()
                );
            } else {
                println!(
                    "✓ Successfully compiled {} to {}",
                    input_file,
                    target.name()
                );
            }
            println!(
                "  Stats: {} tokens, {} AST nodes, {} HIR exprs, {} bytes output",
                result.stats.token_count,
                result.stats.ast_node_count,
                result.stats.hir_expr_count,
                result.stats.output_bytes
            );
        }
        Err(errors) => {
            eprintln!("✗ Compilation failed with {} error(s):", errors.len());
            for (i, error) in errors.iter().enumerate() {
                eprintln!("  [{}] {:?}", i + 1, error);
            }
            std::process::exit(1);
        }
    }
}

fn lex_command(args: &[String]) {
    if args.len() < 3 {
        eprintln!("Usage: magma lex <file>");
        std::process::exit(1);
    }

    let filename = &args[2];
    match fs::read_to_string(filename) {
        Ok(source) => {
            use magma::lexer::Lexer;
            let lexer = Lexer::new(&source);
            match lexer.tokenize() {
                Ok(tokens) => {
                    println!("Tokens from '{}':", filename);
                    for token in tokens {
                        println!("  {}", token);
                    }
                }
                Err(e) => {
                    eprintln!("Error: {:?}", e);
                    std::process::exit(1);
                }
            }
        }
        Err(e) => {
            eprintln!("Error reading file: {}", e);
            std::process::exit(1);
        }
    }
}

fn parse_command(args: &[String]) {
    if args.len() < 3 {
        eprintln!("Usage: magma parse <file>");
        std::process::exit(1);
    }

    let filename = &args[2];
    match fs::read_to_string(filename) {
        Ok(source) => {
            use magma::lexer::Lexer;
            use magma::parser::Parser;

            let lexer = Lexer::new(&source);
            match lexer.tokenize() {
                Ok(tokens) => {
                    let parser = Parser::new(tokens);
                    match parser.parse_program() {
                        Ok(program) => {
                            println!("AST from '{}':", filename);
                            println!("  Items: {}", program.items.len());
                            for (i, item) in program.items.iter().enumerate() {
                                println!("    [{}] {:?}", i + 1, item);
                            }
                        }
                        Err(e) => {
                            eprintln!("Parse error: {:?}", e);
                            std::process::exit(1);
                        }
                    }
                }
                Err(e) => {
                    eprintln!("Lex error: {:?}", e);
                    std::process::exit(1);
                }
            }
        }
        Err(e) => {
            eprintln!("Error reading file: {}", e);
            std::process::exit(1);
        }
    }
}

fn print_usage() {
    eprintln!(
        "Magma Compiler v{}

USAGE:
    magma <COMMAND> [OPTIONS] [ARGS]

COMMANDS:
    compile     Compile Magma source to target language
    lex         Tokenize Magma source file
    parse       Parse Magma source file
    help        Show this help message
    
OPTIONS:
    --target <ts|js|llvm>    Target backend (default: ts)
    --output, -o <FILE>      Output file path
    --help, -h              Show help
    --version, -v           Show version

EXAMPLES:
    magma compile hello.mg
    magma compile --target js hello.mg
    magma compile --target llvm -o output.ll hello.mg
    magma lex hello.mg
    magma parse hello.mg",
        magma::version()
    );
}

fn print_help() {
    print_usage();
}
