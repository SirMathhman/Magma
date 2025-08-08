package magma;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Compiler {
	static final Pattern LET_INT_PATTERN =
			Pattern.compile("^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU])(8|16|32|64)\\s*=\\s*0\\s*;\\s*$");
	// Pattern: let <name> = <number><suffix>; where suffix is [IU](8|16|32|64)
	static final Pattern LET_TYPED_LITERAL_PATTERN =
			Pattern.compile("^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*([IU])(8|16|32|64)\\s*;\\s*$");
	// Pattern: let <name> = <int>; defaults to I32
	static final Pattern LET_DEFAULT_I32_PATTERN =
			Pattern.compile("^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*;\\s*$");
	// Pattern: let <name> = <identifier>;
	static final Pattern LET_FROM_IDENTIFIER_PATTERN =
			Pattern.compile("^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*;\\s*$");

	// New patterns for mut and assignment
	static final Pattern LET_MUT_TYPED_LITERAL_PATTERN =
			Pattern.compile("^\\s*let\\s+mut\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*([IU])(8|16|32|64)\\s*;\\s*$");
	static final Pattern LET_MUT_DEFAULT_I32_PATTERN =
			Pattern.compile("^\\s*let\\s+mut\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*;\\s*$");
	static final Pattern ASSIGN_TYPED_LITERAL_PATTERN =
			Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*([IU])(8|16|32|64)\\s*;\\s*$");
	static final Pattern ASSIGN_DEFAULT_I32_PATTERN =
			Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*;\\s*$");

	// New: conditional (ternary) operator patterns for let and assignment with identifiers or default I32 literals
	static final Pattern LET_CONDITIONAL_PATTERN = Pattern.compile(
			"^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([a-zA-Z_][a-zA-Z0-9_]*|[0-9]+)\\s*\\?\\s*([a-zA-Z_][a-zA-Z0-9_]*|[0-9]+)\\s*:\\s*([a-zA-Z_][a-zA-Z0-9_]*|[0-9]+)\\s*;\\s*$");
	static final Pattern ASSIGN_CONDITIONAL_PATTERN = Pattern.compile(
			"^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([a-zA-Z_][a-zA-Z0-9_]*|[0-9]+)\\s*\\?\\s*([a-zA-Z_][a-zA-Z0-9_]*|[0-9]+)\\s*:\\s*([a-zA-Z_][a-zA-Z0-9_]*|[0-9]+)\\s*;\\s*$");

	public static String compile(String input) throws CompileException {
		if (input == null) throw new CompileException();
		if (input.isEmpty()) return "";
		List<String> statements = splitStatements(input);
		if (statements.isEmpty()) throw new CompileException();
		Map<String, String> types = new LinkedHashMap<>();
		Map<String, Boolean> mutable = new LinkedHashMap<>();
		List<String> outputs = new ArrayList<>();
		for (String stmt : statements) {
			if (!processStatement(stmt, types, mutable, outputs)) throw new CompileException();
		}
		return "#include <stdint.h>\n" + String.join("\n", outputs);
	}

	private static boolean processStatement(String stmt,
																					Map<String, String> types,
																					Map<String, Boolean> mutable,
																					List<String> outputs) throws CompileException {
		if (StatementHandlers.handleLetInt(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleLetTypedLiteral(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleLetDefaultI32(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleLetFromIdentifier(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleLetConditional(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleLetMutTypedLiteral(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleLetMutDefaultI32(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleAssignConditional(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleAssignTypedLiteral(stmt, types, mutable, outputs)) return true;
		return StatementHandlers.handleAssignDefaultI32(stmt, types, mutable, outputs);
	}

	static String toCType(String sign, String bits) {
		String prefix;
		if ("U".equals(sign)) {
			prefix = "uint";
		} else {
			prefix = "int";
		}
		return prefix + bits + "_t";
	}

	// Helpers for conditional operator type resolution
	static boolean isNumberToken(String token) {
		return token.matches("[0-9]+");
	}

	static String resolveExprType(String token, Map<String, String> types) throws CompileException {
		if (isNumberToken(token)) {
			return "int32_t";
		}
		String t = types.get(token);
		if (t == null) throw new CompileException();
		return t;
	}

	static void validateCondition(String token, Map<String, String> types) throws CompileException {
		if (isNumberToken(token)) return;
		if (!types.containsKey(token)) throw new CompileException();
	}

	private static List<String> splitStatements(String input) {
		List<String> statements = new ArrayList<>();
		String[] parts = input.split(";");
		for (String raw : parts) {
			String stmt = raw.trim();
			if (!stmt.isEmpty()) {
				statements.add(stmt + ";");
			}
		}
		return statements;
	}
}
