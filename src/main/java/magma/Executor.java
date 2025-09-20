package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Executor {
	private record PlusOperands(String sum, String leftSuffix, String rightSuffix) {}

	public static Result<String, String> execute(String input) {
		Optional<String> opt = Optional.ofNullable(input).filter(s -> !s.isEmpty());
		if (opt.isEmpty()) {
			return new Result.Ok<>("");
		}
		String s = opt.get().trim();
		// If the whole input is a braced block (matching braces), evaluate the inner sequence directly
		if (s.startsWith("{") && matchingClosingBraceIndex(s) == s.length() - 1) {
			String inner = s.substring(1, s.length() - 1).trim();
			return runSequence(inner);
		}
		return runSequence(s);
	}

	private static Optional<Result<String, String>> processNonFinalStatements(List<String> nonEmpty,
																																						Map<String, String[]> env) {
		if (nonEmpty.size() <= 1) return Optional.empty();
		List<String> toProcess = nonEmpty.subList(0, nonEmpty.size() - 1);
		return processStatements(toProcess, env);
	}

	private static Optional<PlusOperands> parseAndSumStrings(String aStr, String bStr, String aSuffix, String bSuffix) {
		try {
			int a = Integer.parseInt(aStr);
			int b = Integer.parseInt(bStr);
			return Optional.of(new PlusOperands(String.valueOf(a + b), aSuffix, bSuffix));
		} catch (NumberFormatException ex) {
			return Optional.empty();
		}
	}

	private static ArrayList<String> splitNonEmptyStatements(String s) {
		String[] parts = s.split(";");
		ArrayList<String> nonEmpty = new ArrayList<>();
		for (String p : parts) {
			String t = p.trim();
			if (!t.isEmpty()) nonEmpty.add(t);
		}
		return nonEmpty;
	}

	private static Optional<Result<String, String>> handleAllBindingsCase(List<String> nonEmpty,
																																				Map<String, String[]> env) {
		if (nonEmpty.isEmpty()) return Optional.empty();
		String last = nonEmpty.getLast();
		if (last.startsWith("let ") || isAssignmentStatement(last)) {
			Optional<Result<String, String>> buildErr = processStatements(nonEmpty, env);
			if (buildErr.isPresent()) return buildErr;
			return Optional.of(new Result.Ok<>(""));
		}
		return Optional.empty();
	}

	private static Result<String, String> runSequence(String s) {
		ArrayList<String> nonEmpty = splitNonEmptyStatements(s);
		HashMap<String, String[]> env = new HashMap<>();
		if (nonEmpty.isEmpty()) return new Result.Ok<>("");
		Optional<Result<String, String>> maybe = handleAllBindingsCase(nonEmpty, env);
		if (maybe.isPresent()) return maybe.get();
		Optional<Result<String, String>> buildErr = processNonFinalStatements(nonEmpty, env);
		return buildErr.orElseGet(() -> evaluateFinal(nonEmpty.getLast(), env));
	}

	private static Optional<Result<String, String>> processStatements(List<String> stmts, Map<String, String[]> env) {
		for (String stmt : stmts) {
			Optional<Result<String, String>> err;
			if (stmt.startsWith("let ")) {
				err = processSingleLet(stmt, env);
			} else if (isAssignmentStatement(stmt)) {
				err = processAssignment(stmt, env);
			} else {
				err = Optional.of(new Result.Err<>("Invalid statement: " + stmt));
			}
			if (err.isPresent()) return err;
		}
		return Optional.empty();
	}

	private static Optional<Result<String, String>> processSingleLet(String stmt, Map<String, String[]> env) {
		if (!stmt.startsWith("let ")) return createErr("Expected 'let' declaration");
		int eq = stmt.indexOf('=', 4);
		String afterLet = stmt.substring(4, (eq > 4 ? eq : stmt.length())).trim();
		boolean isMutable = false;
		String lhs = afterLet;
		if (afterLet.startsWith("mut ")) {
			isMutable = true;
			lhs = afterLet.substring(4).trim();
		}
		String ident = extractIdentFromLhs(lhs);
		if (env.containsKey(ident)) return createErr("Duplicate binding");
		int colonPos = lhs.indexOf(':');
		String declared = "";
		if (colonPos > 0) declared = lhs.substring(colonPos + 1).trim();
		// Declared types must not start with '&' (use & only in RHS expressions)
		if (declared.startsWith("&")) return createErr("Invalid declared type: '" + declared + "'");
		// If there is no '=', this is a declaration without initializer
		if (eq <= 4) {
			// must have declared type
			if (declared.isEmpty()) return createErr("Missing declared type for variable '" + ident + "'");
			// Declaration without initializer: mark as deferred (assign-once) unless 'mut'
			// was present
			String mut = isMutable ? "mutable" : "deferred";
			String[] entry = new String[]{"", declared, mut};
			env.put(ident, entry);
			return Optional.empty();
		}
		String rhs = stmt.substring(eq + 1).trim();
		Optional<Result<String, String>> evalResult = evaluateAndValidateRhs(rhs, declared, env);
		if (evalResult.isPresent()) return evalResult;
		final Optional<String[]> strings = evaluateRhsExpression(rhs, env);
		if (strings.isEmpty()) return Optional.empty();
		String[] pair = strings.get(); // Safe since we validated above
		// Store [value, suffix, mutability]
		String[] entry = new String[]{pair[0], pair[1], isMutable ? "mutable" : "immutable"};
		env.put(ident, entry);
		return Optional.empty();
	}

	private static Optional<Result<String, String>> evaluateAndValidateRhs(String rhs,
																																				 String declared,
																																				 Map<String, String[]> env) {
		Optional<String[]> rhsResult = evaluateRhsExpression(rhs, env);
		if (rhsResult.isEmpty()) return rhsError(rhs);
		String[] pair = rhsResult.get();
		String suffix = pair[1];
		// If declared is present and RHS has no suffix (e.g. literal like true), accept
		// it
		if (!Objects.isNull(declared) && !declared.isEmpty()) {
			if (Objects.isNull(suffix) || suffix.isEmpty()) {
				return Optional.empty();
			}
		}
		if (isNotDeclaredCompatible(declared, suffix)) return createErr("Declared type does not match expression suffix");
		return Optional.empty();
	}

	private static Optional<String[]> evaluateRhsExpression(String rhs, Map<String, String[]> env) {
		if (rhs.isEmpty()) return Optional.empty();
		return parseRhsPair(rhs, env);
	}

	private static Optional<String[]> parseRhsPair(String rhs, Map<String, String[]> env) {
		if (rhs.startsWith("&")) {
			// support &mut and & (immutable) references
			if (rhs.startsWith("&mut")) {
				String pointee = rhs.substring(4).trim();
				if (!env.containsKey(pointee)) return Optional.empty();
				String pointeeSuffix = env.get(pointee)[1];
				return Optional.of(new String[]{pointee, "*mut " + pointeeSuffix});
			}
			String pointee = rhs.substring(1).trim();
			if (!env.containsKey(pointee)) return Optional.empty();
			String pointeeSuffix = env.get(pointee)[1];
			return Optional.of(new String[]{pointee, "*" + pointeeSuffix});
		}
		Optional<String[]> rhsOpt = evaluateSingleWithSuffix(rhs);
		if (rhsOpt.isPresent()) return rhsOpt;
		if (!env.containsKey(rhs)) return Optional.empty();
		// Return only [value, suffix] from environment entry
		String[] entry = env.get(rhs);
		return Optional.of(new String[]{entry[0], entry[1]});
	}

	private static Optional<Result<String, String>> rhsError(String rhs) {
		return Optional.of(new Result.Err<>("Invalid RHS expression: '" + rhs + "'"));
	}

	private static Optional<Result<String, String>> createErr(String msg) {
		return Optional.of(new Result.Err<>(msg));
	}

	private static boolean isNotDeclaredCompatible(String declared, String suffix) {
		if (Objects.isNull(declared) || declared.isEmpty()) return false;
		if (declared.startsWith("*")) {
			String baseDeclared = declared.substring(1).trim();
			String pointeeSuffix = suffix.startsWith("*") ? suffix.substring(1) : suffix;
			return !checkBasePointeeCompatibility(baseDeclared, pointeeSuffix);
		}
		return !declared.equals(suffix);
	}

	private static boolean checkBasePointeeCompatibility(String baseDeclared, String pointeeSuffix) {
		if (baseDeclared.isEmpty() || pointeeSuffix.isEmpty()) return true;
		// Normalize and handle 'mut' prefix differences like "mut I32" vs "mut "
		String b = baseDeclared.trim();
		String p = pointeeSuffix.trim();
		if (b.startsWith("mut") && p.startsWith("mut")) {
			String bRest = b.substring(3).trim();
			String pRest = p.substring(3).trim();
			if (bRest.isEmpty() || pRest.isEmpty()) return true;
			return bRest.equals(pRest);
		}
		return b.equals(p);
	}

	private static boolean isAssignmentStatement(String stmt) {
		// Assignment: ident = expr (no 'let' prefix and contains '=')
		if (stmt.startsWith("let ")) return false;
		int eq = stmt.indexOf('=');
		if (eq <= 0) return false;
		String lhs = stmt.substring(0, eq).trim();
		// Simple identifier check - should not contain spaces or special chars except
		// ':'
		return lhs.matches("[a-zA-Z_][a-zA-Z0-9_]*");
	}

	private static Optional<Result<String, String>> processAssignment(String stmt, Map<String, String[]> env) {
		int eq = stmt.indexOf('=');
		if (eq <= 0) return createErr("Invalid assignment syntax");
		String ident = stmt.substring(0, eq).trim();
		if (!env.containsKey(ident)) return createErr("Unknown identifier: '" + ident + "'");
		String[] entry = env.get(ident);
		String mutFlag = entry[2];
		String lhsSuffix = entry[1];
		if (!isLhsAssignable(entry)) return createErr("Assignment target is not assignable");
		String rhs = stmt.substring(eq + 1).trim();
		Optional<String[]> rhsEval = evaluateRhsExpression(rhs, env);
		if (rhsEval.isEmpty()) return rhsError(rhs);
		String[] pair = rhsEval.get();
		String rhsSuffix = pair[1];
		String declaredSuffix = entry[1];
		Optional<Result<String, String>> compatErr = validateDeclaredCompatibility(declaredSuffix, rhsSuffix);
		if (compatErr.isPresent()) return compatErr;
		// If the LHS is a pointer variable with '*mut' suffix, update the pointee
		// instead
		if (!Objects.isNull(lhsSuffix) && lhsSuffix.startsWith("*mut")) {
			return handleDerefAssignment(entry, pair, env);
		}
		// Determine new mutability: deferred -> immutable after first assignment;
		// mutable remains mutable
		String newMut = "immutable".equals(mutFlag) ? "immutable" : ("deferred".equals(mutFlag) ? "immutable" : "mutable");
		String[] newEntry = new String[]{pair[0], pair[1], newMut};
		env.put(ident, newEntry);
		return Optional.empty();
	}

	private static Optional<Result<String, String>> handleDerefAssignment(String[] pointerEntry,
																																				String[] rhsPair,
																																				Map<String, String[]> env) {
		// pointerEntry[0] holds the pointee name
		String pointeeName = pointerEntry[0];
		if (Objects.isNull(pointeeName) || pointeeName.isEmpty()) return createErr("Pointer target not specified");
		if (!env.containsKey(pointeeName)) return createErr("Pointer target not found: '" + pointeeName + "'");
		String[] pointeeEntry = env.get(pointeeName);
		// update pointee value and set its suffix to RHS suffix, keep mutability
		String[] updated = new String[]{rhsPair[0], rhsPair[1], pointeeEntry[2]};
		env.put(pointeeName, updated);
		return Optional.empty();
	}

	private static boolean isLhsAssignable(String[] entry) {
		String mutFlag = entry[2];
		String lhsSuffix = entry[1];
		// If the LHS is a pointer variable with '*mut' suffix, allow deref-assignment
		if (!Objects.isNull(lhsSuffix) && lhsSuffix.startsWith("*mut")) return true;
		return "mutable".equals(mutFlag) || "deferred".equals(mutFlag);
	}

	private static Optional<Result<String, String>> validateDeclaredCompatibility(String declaredSuffix,
																																								String rhsSuffix) {
		if (!Objects.isNull(declaredSuffix) && !declaredSuffix.isEmpty()) {
			// If RHS has a suffix, enforce compatibility; if RHS suffix is empty, accept
			// and rely on declared type
			if (!Objects.isNull(rhsSuffix) && !rhsSuffix.isEmpty()) {
				if (isNotDeclaredCompatible(declaredSuffix, rhsSuffix))
					return Optional.of(new Result.Err<>("Declared type does not match expression suffix"));
			}
		}
		return Optional.empty();
	}

	private static Result<String, String> evaluateFinal(String stmt, Map<String, String[]> env) {
		Optional<Result<String, String>> br = evaluateBracedFinal(stmt, env);
		if (br.isPresent()) return br.get();
		// if it's an identifier, return its value from env
		if (env.containsKey(stmt)) {
			String[] entry = env.get(stmt);
			// If the binding exists but has empty value and a declared suffix, treat as
			// uninitialized
			String val = entry[0];
			String declaredSuffix = entry[1];
			if ((Objects.isNull(val) || val.isEmpty()) && !Objects.isNull(declaredSuffix) && !declaredSuffix.isEmpty()) {
				return new Result.Err<>("Uninitialized variable '" + stmt + "'");
			}
			return new Result.Ok<>(entry[0]);
		}
		// pointer dereference expression like *y
		if (stmt.startsWith("*")) {
			String ref = stmt.substring(1).trim();
			if (!env.containsKey(ref)) return new Result.Err<>("Unknown pointer variable: '" + ref + "'");
			String target = env.get(ref)[0];
			if (env.containsKey(target)) {
				String[] entry = env.get(target);
				return new Result.Ok<>(entry[0]);
			}
			return new Result.Err<>("Non-empty input not allowed");
		}
		// otherwise evaluate as single expression
		return evaluateSingle(stmt);
	}

	private static Optional<Result<String, String>> evaluateBracedFinal(String stmt, Map<String, String[]> env) {
		if (!stmt.startsWith("{") || matchingClosingBraceIndex(stmt) != stmt.length() - 1) return Optional.empty();
		String inner = stmt.substring(1, stmt.length() - 1).trim();
		ArrayList<String> nonEmpty = splitNonEmptyStatements(inner);
		if (nonEmpty.isEmpty()) return Optional.of(new Result.Ok<>(""));
		Optional<Result<String, String>> maybe = handleAllBindingsCase(nonEmpty, env);
		if (maybe.isPresent()) return maybe;
		Optional<Result<String, String>> buildErr = processNonFinalStatements(nonEmpty, env);
		if (buildErr.isPresent()) return buildErr;
		return Optional.of(evaluateFinal(nonEmpty.getLast(), env));
	}

	private static Result<String, String> evaluateSingle(String s) {
		Optional<Result<String, String>> resOpt = evaluateArithmeticOrLeading(s);
		if (resOpt.isPresent()) return resOpt.get();
		// try single-expression forms that also return a suffix (booleans, if-expr,
		// leading with suffix)
		Optional<String[]> pairOpt = evaluateSingleWithSuffix(s);
		if (pairOpt.isPresent()) {
			String[] pair = pairOpt.get();
			return new Result.Ok<>(pair[0]);
		}
		return new Result.Err<>("Invalid expression: '" + s + "'");
	}

	/**
	 * Evaluate the single expression and also return the suffix of the resulting
	 * value if present. Returns Optional of String[2] where [0]=value and
	 * [1]=suffix
	 * (empty string if none). Returns empty Optional if evaluation failed.
	 */
	private static Optional<String[]> evaluateSingleWithSuffix(String s) {
		// Try arithmetic or leading-digit parsing and also provide suffix
		Optional<String[]> arithOpt = evaluateArithmeticWithSuffix(s);
		if (arithOpt.isPresent()) return arithOpt;
		// leading digits case
		Optional<String[]> leading = extractLeadingDigits(s);
		if (leading.isPresent()) {
			return Optional.of(new String[]{leading.get()[0], leading.get()[1]});
		}
		// boolean literals
		if ("true".equals(s) || "false".equals(s)) {
			return Optional.of(new String[]{s, ""});
		}
		Optional<String[]> ifOpt = evaluateIfWithSuffix(s);
		if (ifOpt.isPresent()) return ifOpt;
		return evaluateBracedWithSuffix(s);
	}

	private static Optional<String[]> evaluateIfWithSuffix(String s) {
		if (!s.startsWith("if (")) return Optional.empty();
		int close = s.indexOf(')', 4);
		if (close <= 4) return Optional.empty();
		String condExpr = s.substring(4, close).trim();
		String rest = s.substring(close + 1).trim();
		int elseIdx = rest.indexOf("else");
		if (elseIdx <= 0) return Optional.empty();
		String thenExpr = rest.substring(0, elseIdx).trim();
		String elseExpr = rest.substring(elseIdx + 4).trim();
		Optional<String[]> condPairOpt = evaluateSingleWithSuffix(condExpr);
		if (condPairOpt.isPresent()) {
			String[] condPair = condPairOpt.get();
			String condVal = condPair[0];
			boolean takeThen = "true".equals(condVal);
			String chosen = takeThen ? thenExpr : elseExpr;
			return evaluateSingleWithSuffix(chosen);
		}
		return Optional.empty();
	}

	private static Optional<String[]> evaluateBracedWithSuffix(String s) {
		if (!s.startsWith("{") || !s.endsWith("}")) return Optional.empty();
		String inner = s.substring(1, s.length() - 1).trim();
		if (inner.isEmpty()) return Optional.empty();
		if (inner.contains(";") || inner.startsWith("let ")) {
			Result<String, String> res = runSequence(inner);
			if (res instanceof Result.Ok(Object value)) {
				return Optional.of(new String[]{String.valueOf(value), ""});
			}
			return Optional.empty();
		}
		return evaluateSingleWithSuffix(inner);
	}

	private static int matchingClosingBraceIndex(String s) {
		int depth = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '{') depth++;
			else if (c == '}') {
				depth--;
				if (depth == 0) return i;
			}
		}
		return -1;
	}

	// Helper that consolidates arithmetic and leading-digit handling returning
	// Optional<Result> empty when not applicable. This removes duplication detected
	// by CPD.
	private static Optional<Result<String, String>> evaluateArithmeticOrLeading(String s) {
		// arithmetic case handled by shared parser
		Optional<PlusOperands> opOpt = parsePlusOperands(s);
		if (opOpt.isPresent()) {
			PlusOperands op = opOpt.get();
			if (!op.leftSuffix.equals(op.rightSuffix)) {
				return Optional.of(new Result.Err<>("Mismatched operand suffixes"));
			}
			return Optional.of(new Result.Ok<>(op.sum));
		}
		// leading digits
		Optional<String[]> leading = extractLeadingDigits(s);
		return leading.map(strings -> new Result.Ok<>(strings[0]));
	}

	// Helper that evaluates arithmetic and returns Optional of [value,suffix]
	// empty when not applicable
	private static Optional<String[]> evaluateArithmeticWithSuffix(String s) {
		Optional<PlusOperands> opOpt = parsePlusOperands(s);
		if (opOpt.isEmpty()) return Optional.empty();
		PlusOperands op = opOpt.get();
		if (!op.leftSuffix.equals(op.rightSuffix)) return Optional.empty();
		return Optional.of(new String[]{op.sum, op.leftSuffix});
	}

	// Parse a plus expression like "1U8 + 2U8" and return sum and suffixs when
	// parsable
	private static Optional<PlusOperands> parsePlusOperands(String s) {
		int plusIndex = s.indexOf('+');
		if (plusIndex >= 0 && plusIndex == s.lastIndexOf('+')) {
			String left = s.substring(0, plusIndex).trim();
			String right = s.substring(plusIndex + 1).trim();
			String leftNum = leadingInteger(left);
			String rightNum = leadingInteger(right);
			if (!leftNum.isEmpty() && !rightNum.isEmpty()) {
				String leftSuffix = left.substring(leftNum.length());
				String rightSuffix = right.substring(rightNum.length());
				return parseAndSumStrings(leftNum, rightNum, leftSuffix, rightSuffix);
			}
			// Try evaluating each side as a full expression (handles braced expressions)
			Optional<String[]> leftPairOpt = evaluateSingleWithSuffix(left);
			Optional<String[]> rightPairOpt = evaluateSingleWithSuffix(right);
			if (leftPairOpt.isPresent() && rightPairOpt.isPresent()) {
				String[] leftPair = leftPairOpt.get();
				String[] rightPair = rightPairOpt.get();
				String leftVal = leftPair[0];
				String rightVal = rightPair[0];
				String leftSuffix = leftPair[1];
				String rightSuffix = rightPair[1];
				return parseAndSumStrings(leftVal, rightVal, leftSuffix, rightSuffix);
			}
		}
		return Optional.empty();
	}

	private static String leadingInteger(String s) {
		if (Objects.isNull(s) || s.isEmpty()) {
			return "";
		}
		int idx = 0;
		char c = s.charAt(0);
		if (c == '+' || c == '-') {
			idx = 1;
		}
		while (idx < s.length() && Character.isDigit(s.charAt(idx))) {
			idx++;
		}
		return s.substring(0, idx);
	}

	private static Optional<String[]> extractLeadingDigits(String s) {
		if (Objects.isNull(s) || s.isEmpty()) return Optional.empty();
		int i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i))) {
			i++;
		}
		if (i > 0) {
			String num = s.substring(0, i);
			String suffix = s.substring(i);
			return Optional.of(new String[]{num, suffix});
		}
		return Optional.empty();
	}

	private static String extractIdentFromLhs(String lhs) {
		String ident = lhs;
		int colon = lhs.indexOf(':');
		if (colon > 0) {
			ident = lhs.substring(0, colon).trim();
		}
		return ident;
	}
}