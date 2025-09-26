package magma.compile;

import java.util.List;

import magma.api.Result;

/**
 * High-level compiler entry point that transforms Magma source into C code.
 */
public final class Compiler {
	public Result<String, CompileError> compile(String source) {
		Lexer lexer = new Lexer(source);
		Lexer.Result lexResult = lexer.lex();
		if (!lexResult.errors().isEmpty()) {
			return Result.err(error("Lexing", lexResult.errors()));
		}

		Parser parser = new Parser(lexResult.tokens());
		Parser.Result parseResult = parser.parseProgram();
		if (!parseResult.errors().isEmpty()) {
			return Result.err(error("Parsing", parseResult.errors()));
		}

		SemanticAnalyzer analyzer = new SemanticAnalyzer();
		SemanticAnalyzer.AnalysisResult analysisResult = analyzer.analyze(parseResult.program());
		if (!analysisResult.errors().isEmpty()) {
			return Result.err(error("Semantic analysis", analysisResult.errors()));
		}

		CodeGenerator generator = new CodeGenerator();
		String cCode = generator.generate(analysisResult);
		return Result.ok(cCode);
	}

	private CompileError error(String stage, List<String> messages) {
		String combined = stage + " failed: " + String.join("; ", messages);
		return new CompileError(combined);
	}
}
