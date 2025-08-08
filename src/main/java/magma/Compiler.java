package magma;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_INT_PATTERN =
			Pattern.compile("^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU])(8|16|32|64)\\s*=\\s*0\\s*;\\s*$");
	// Pattern: let <name> = <number><suffix>; where suffix is [IU](8|16|32|64)
	private static final Pattern LET_TYPED_LITERAL_PATTERN =
			Pattern.compile("^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*([IU])(8|16|32|64)\\s*;\\s*$");
	// Pattern: let <name> = <int>; defaults to I32
	private static final Pattern LET_DEFAULT_I32_PATTERN =
			Pattern.compile("^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*;\\s*$");
	// Pattern: let <name> = <identifier>;
	private static final Pattern LET_FROM_IDENTIFIER_PATTERN =
			Pattern.compile("^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*;\\s*$");

	// New patterns for mut and assignment
	private static final Pattern LET_MUT_TYPED_LITERAL_PATTERN =
			Pattern.compile("^\\s*let\\s+mut\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*([IU])(8|16|32|64)\\s*;\\s*$");
	private static final Pattern LET_MUT_DEFAULT_I32_PATTERN =
			Pattern.compile("^\\s*let\\s+mut\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*;\\s*$");
	private static final Pattern ASSIGN_TYPED_LITERAL_PATTERN =
			Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*([IU])(8|16|32|64)\\s*;\\s*$");
	private static final Pattern ASSIGN_DEFAULT_I32_PATTERN =
			Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*;\\s*$");

	public static String compile(String input) throws CompileException {
		if (input == null) throw new CompileException();
		if (input.isEmpty()) return "";

		List<String> statements = splitStatements(input);
		if (statements.isEmpty()) throw new CompileException();

		Map<String, String> types = new LinkedHashMap<>(); // varName -> CType
		Map<String, Boolean> mutable = new LinkedHashMap<>(); // varName -> isMutable
		List<String> outputs = new ArrayList<>();

		for (String stmt : statements) {
			Matcher m;

			// Existing patterns (immutable typed with = 0)
			m = LET_INT_PATTERN.matcher(stmt);
			if (m.matches()) {
				String name = m.group(1);
				String sign = m.group(2);
				String bits = m.group(3);
				String cType = toCType(sign, bits);
				types.put(name, cType);
				mutable.put(name, Boolean.FALSE);
				outputs.add(cType + " " + name + " = 0;");
				continue;
			}

			// Immutable let with typed literal suffix
			m = LET_TYPED_LITERAL_PATTERN.matcher(stmt);
			if (m.matches()) {
				String name = m.group(1);
				String value = m.group(2);
				String sign = m.group(3);
				String bits = m.group(4);
				String cType = toCType(sign, bits);
				types.put(name, cType);
				mutable.put(name, Boolean.FALSE);
				outputs.add(cType + " " + name + " = " + value + ";");
				continue;
			}

			// Immutable let default I32
			m = LET_DEFAULT_I32_PATTERN.matcher(stmt);
			if (m.matches()) {
				String name = m.group(1);
				String value = m.group(2);
				types.put(name, "int32_t");
				mutable.put(name, Boolean.FALSE);
				outputs.add("int32_t " + name + " = " + value + ";");
				continue;
			}

			// Immutable let from identifier
			m = LET_FROM_IDENTIFIER_PATTERN.matcher(stmt);
			if (m.matches()) {
				String name = m.group(1);
				String rhs = m.group(2);
				String rhsType = types.get(rhs);
				if (rhsType == null) throw new CompileException();
				types.put(name, rhsType);
				mutable.put(name, Boolean.FALSE);
				outputs.add(rhsType + " " + name + " = " + rhs + ";");
				continue;
			}

			// NEW: let mut with typed literal suffix
			m = LET_MUT_TYPED_LITERAL_PATTERN.matcher(stmt);
			if (m.matches()) {
				String name = m.group(1);
				String value = m.group(2);
				String sign = m.group(3);
				String bits = m.group(4);
				String cType = toCType(sign, bits);
				types.put(name, cType);
				mutable.put(name, Boolean.TRUE);
				outputs.add(cType + " " + name + " = " + value + ";");
				continue;
			}

			// NEW: let mut default I32
			m = LET_MUT_DEFAULT_I32_PATTERN.matcher(stmt);
			if (m.matches()) {
				String name = m.group(1);
				String value = m.group(2);
				types.put(name, "int32_t");
				mutable.put(name, Boolean.TRUE);
				outputs.add("int32_t " + name + " = " + value + ";");
				continue;
			}

			// NEW: assignment of typed literal suffix
			m = ASSIGN_TYPED_LITERAL_PATTERN.matcher(stmt);
			if (m.matches()) {
				String name = m.group(1);
				String value = m.group(2);
				String sign = m.group(3);
				String bits = m.group(4);
				String varType = types.get(name);
				Boolean isMut = mutable.get(name);
				if (varType == null || isMut == null || !isMut) throw new CompileException();
				String literalType = toCType(sign, bits);
				if (!literalType.equals(varType)) throw new CompileException();
				outputs.add(name + " = " + value + ";");
				continue;
			}

			// NEW: assignment of default I32 literal
			m = ASSIGN_DEFAULT_I32_PATTERN.matcher(stmt);
			if (m.matches()) {
				String name = m.group(1);
				String value = m.group(2);
				String varType = types.get(name);
				Boolean isMut = mutable.get(name);
				if (varType == null || isMut == null || !isMut) throw new CompileException();
				// Only allow default literal assignment when variable is int32_t
				if (!"int32_t".equals(varType)) throw new CompileException();
				outputs.add(name + " = " + value + ";");
				continue;
			}

			// If no pattern matched for this statement
			throw new CompileException();
		}

		return "#include <stdint.h>\n" + String.join("\n", outputs);
	}

	private static String toCType(String sign, String bits) {
		String prefix;
		if ("U".equals(sign)) {
			prefix = "uint";
		} else {
			prefix = "int";
		}
		return prefix + bits + "_t";
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
