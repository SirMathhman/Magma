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

	// New: pointer declaration with address-of initializer: let <name> : *[IU](8|16|32|64) = &<ident>;
	static final Pattern LET_PTR_ADDR_PATTERN = Pattern.compile(
			"^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*\\*([IU])(8|16|32|64)\\s*=\\s*&\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*;\\s*$");
	// New: typed let from dereference: let <name> : [IU](8|16|32|64) = *<ident>;
	static final Pattern LET_FROM_DEREF_PATTERN = Pattern.compile(
			"^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU])(8|16|32|64)\\s*=\\s*\\*\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*;\\s*$");

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
		// pointer let handlers
		if (handlePointerLet(stmt, types, mutable, outputs)) return true;
		if (StatementHandlers.handleLetMut(stmt, types, mutable, outputs)) return true;
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

	// Pointer let handlers consolidated here to satisfy Checkstyle limits
	static boolean handlePointerLet(String stmt,
										Map<String, String> types,
										Map<String, Boolean> mutable,
										List<String> outputs) throws CompileException {
		java.util.regex.Matcher m1 = LET_PTR_ADDR_PATTERN.matcher(stmt);
		if (m1.matches()) {
			String base = toCType(m1.group(2), m1.group(3)); String rhs = m1.group(4);
			String rhsType = types.get(rhs); if (!base.equals(rhsType)) throw new CompileException();
			String ptr = base + "*"; types.put(m1.group(1), ptr); mutable.put(m1.group(1), Boolean.FALSE);
			outputs.add(ptr + " " + m1.group(1) + " = &" + rhs + ";"); return true;
		}
		java.util.regex.Matcher m2 = LET_FROM_DEREF_PATTERN.matcher(stmt); if (!m2.matches()) return false;
		String base = toCType(m2.group(2), m2.group(3)); String rhs = m2.group(4);
		String rhsType = types.get(rhs); if (rhsType == null || !rhsType.equals(base + "*")) throw new CompileException();
		types.put(m2.group(1), base); mutable.put(m2.group(1), Boolean.FALSE);
		outputs.add(base + " " + m2.group(1) + " = *" + rhs + ";"); return true;
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
