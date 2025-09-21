package magma;

import magma.Option.None;
import magma.Option.Some;
import magma.Result.Err;
import magma.Result.Ok;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Executor {
	private record PlusOperands(String sum, String leftSuffix, String rightSuffix) {
	}

	private static Option<Result<String, String>> handleCompoundPlusAssignment(String ident, String[] entry,
			String[] rhsPair, Map<String, String[]> env) {
		var lhsSuffix = entry[1];
		var mutFlag = entry[2];
		// Do not support pointer-target compound assignment here
		if (!Objects.isNull(lhsSuffix) && lhsSuffix.startsWith("*mut")) {
			return createErr("Compound assignment not supported on pointer target");
		}
		// Left-hand current value
		var lhsVal = entry[0];
		if (Objects.isNull(lhsVal) || lhsVal.isEmpty())
			return createErr("Uninitialized variable '" + ident + "'");
		// Try numeric addition using the helper
		var sumOpt = parseAndSumStrings(lhsVal, rhsPair[0], lhsSuffix, rhsPair[1]);
		if (!(sumOpt instanceof Some<PlusOperands>(var sumOperands)))
			return createErr("Invalid compound assignment operands");
		env.put(ident, makeNewEntry(sumOperands.sum, sumOperands.leftSuffix, mutFlag));
		return new None<>();
	}

	public static Result<String, String> execute(String input) {
		Some<String> stringOption = new Some<>(input);
		var opt = !stringOption.value().isEmpty() ? stringOption : new None<String>();
		if (opt instanceof None<String>) {
			return new Ok<>("");
		}
		if (opt instanceof Some<String> someString) {
			var s = someString.value().trim();
			// If the whole input is a braced block (matching braces), evaluate the inner
			// sequence directly
			if (s.startsWith("{") && matchingClosingBraceIndex(s) == s.length() - 1) {
				var inner = s.substring(1, s.length() - 1).trim();
				return runSequence(inner);
			}
			return runSequence(s);
		}
		return new Ok<>(""); // This should never be reached but needed for compilation
	}

	private static Option<Result<String, String>> processNonFinalStatements(List<String> nonEmpty,
			Map<String, String[]> env) {
		if (nonEmpty.size() <= 1)
			return new None<>();
		var toProcess = nonEmpty.subList(0, nonEmpty.size() - 1);
		return processStatements(toProcess, env);
	}

	private static Option<PlusOperands> parseAndSumStrings(String aStr, String bStr, String aSuffix, String bSuffix) {
		try {
			var a = Integer.parseInt(aStr);
			var b = Integer.parseInt(bStr);
			return new Some<>(new PlusOperands(String.valueOf(a + b), aSuffix, bSuffix));
		} catch (NumberFormatException ex) {
			return new None<>();
		}
	}

	private static ArrayList<String> splitNonEmptyStatements(String s) {
		var nonEmpty = new ArrayList<String>();
		var start = 0;
		var depth = 0;

		for (var i = 0; i < s.length(); i++) {
			var c = s.charAt(i);
			depth = updateBraceDepth(depth, c);
			if (isSemicolonAtTopLevel(c, depth)) {
				var statement = s.substring(start, i).trim();
				if (!statement.isEmpty()) {
					nonEmpty.add(statement);
				}
				start = i + 1;
			}
		}

		// Add the remaining part after the last semicolon
		var lastStatement = s.substring(start).trim();
		if (!lastStatement.isEmpty()) {
			nonEmpty.add(lastStatement);
		}

		return nonEmpty;
	}

	private static boolean isSemicolonAtTopLevel(char c, int depth) {
		return c == ';' && depth == 0;
	}

	private static int updateBraceDepth(int depth, char c) {
		if (c == '{') {
			return depth + 1;
		} else if (c == '}') {
			return depth - 1;
		}
		return depth;
	}

	private static Option<Result<String, String>> handleAllBindingsCase(List<String> nonEmpty,
			Map<String, String[]> env) {
		if (nonEmpty.isEmpty())
			return new None<>();
		var last = nonEmpty.getLast();
		if (last.startsWith("let ") || isAssignmentStatement(last)) {
			var buildErr = processStatements(nonEmpty, env);
			if (buildErr instanceof Some<Result<String, String>>)
				return buildErr;
			return new Some<>(new Ok<>(""));
		}
		return new None<>();
	}

	private static Result<String, String> runSequence(String s) {
		var nonEmpty = splitNonEmptyStatements(s);
		var env = new HashMap<String, String[]>();
		if (nonEmpty.isEmpty())
			return new Ok<>("");
		var maybe = handleAllBindingsCase(nonEmpty, env);
		if (maybe instanceof Some<Result<String, String>>(var value)) {
			return value;
		}
		var buildErr = processNonFinalStatements(nonEmpty, env);
		return switch (buildErr) {
			case None<Result<String, String>> _ -> evaluateFinal(nonEmpty.getLast(), env);
			case Some<Result<String, String>>(Result<String, String> value) -> value;
		};
	}

	private static Option<Result<String, String>> processStatements(List<String> stmts, Map<String, String[]> env) {
		for (var stmt : stmts) {
			Option<Result<String, String>> err;
			if (stmt.startsWith("let ")) {
				err = processSingleLet(stmt, env);
			} else if (isAssignmentStatement(stmt)) {
				err = processAssignment(stmt, env);
			} else {
				err = new Some<>(new Err<>("Invalid statement: " + stmt));
			}
			if (err instanceof Some<Result<String, String>>)
				return err;
		}
		return new None<>();
	}

	private static Option<Result<String, String>> processSingleLet(String stmt, Map<String, String[]> env) {
		if (!stmt.startsWith("let "))
			return createErr("Expected 'let' declaration");
		var eq = stmt.indexOf('=', 4);
		var afterLet = stmt.substring(4, (eq > 4 ? eq : stmt.length())).trim();
		var isMutable = false;
		var lhs = afterLet;
		if (afterLet.startsWith("mut ")) {
			isMutable = true;
			lhs = afterLet.substring(4).trim();
		}
		var ident = extractIdentFromLhs(lhs);
		if (env.containsKey(ident))
			return createErr("Duplicate binding");
		var colonPos = lhs.indexOf(':');
		var declared = "";
		if (colonPos > 0)
			declared = lhs.substring(colonPos + 1).trim();
		// Declared types must not start with '&' (use & only in RHS expressions)
		if (declared.startsWith("&"))
			return createErr("Invalid declared type: '" + declared + "'");
		// If there is no '=', this is a declaration without initializer
		if (eq <= 4) {
			// must have declared type
			if (declared.isEmpty())
				return createErr("Missing declared type for variable '" + ident + "'");
			// Declaration without initializer: mark as deferred (assign-once) unless 'mut'
			// was present
			var mut = isMutable ? "mutable" : "deferred";
			var entry = new String[] { "", declared, mut };
			env.put(ident, entry);
			return new None<>();
		}
		var rhs = stmt.substring(eq + 1).trim();
		var evalResult = evaluateAndValidateRhs(rhs, declared, env);
		if (evalResult instanceof Some<Result<String, String>>)
			return evalResult;
		final var strings = evaluateRhsExpression(rhs, env);
		if (!(strings instanceof Some<String[]>(var pair)))
			return new None<>();
		// Safe since we validated above
		// Store [value, suffix, mutability]
		var entry = new String[] { pair[0], pair[1], isMutable ? "mutable" : "immutable" };
		env.put(ident, entry);
		return new None<>();
	}

	private static Option<Result<String, String>> evaluateAndValidateRhs(String rhs,
			String declared,
			Map<String, String[]> env) {
		var rhsResult = evaluateRhsExpression(rhs, env);
		if (!(rhsResult instanceof Some<String[]>(var pair)))
			return rhsError(rhs);
		var suffix = pair[1];
		// If declared is present and RHS has no suffix (e.g. literal like true), accept
		// it
		if (!Objects.isNull(declared) && !declared.isEmpty()) {
			if (Objects.isNull(suffix) || suffix.isEmpty()) {
				return new None<>();
			}
		}
		if (isNotDeclaredCompatible(declared, suffix))
			return createErr("Declared type does not match expression suffix");
		return new None<>();
	}

	private static Option<String[]> evaluateRhsExpression(String rhs, Map<String, String[]> env) {
		if (rhs.isEmpty())
			return new None<>();
		return parseRhsPair(rhs, env);
	}

	private static Option<String[]> parseRhsPair(String rhs, Map<String, String[]> env) {
		if (rhs.startsWith("&")) {
			// support &mut and & (immutable) references
			if (rhs.startsWith("&mut")) {
				var pointee = rhs.substring(4).trim();
				if (!env.containsKey(pointee))
					return new None<>();
				var pointeeSuffix = env.get(pointee)[1];
				return new Some<>(new String[] { pointee, "*mut " + pointeeSuffix });
			}
			var pointee = rhs.substring(1).trim();
			if (!env.containsKey(pointee))
				return new None<>();
			var pointeeSuffix = env.get(pointee)[1];
			return new Some<>(new String[] { pointee, "*" + pointeeSuffix });
		}
		var rhsOpt = evaluateSingleWithSuffix(rhs);
		if (rhsOpt instanceof Some<String[]>)
			return rhsOpt;
		if (!env.containsKey(rhs))
			return new None<>();
		// Return only [value, suffix] from environment entry
		var entry = env.get(rhs);
		return new Some<>(new String[] { entry[0], entry[1] });
	}

	private static Option<Result<String, String>> rhsError(String rhs) {
		return new Some<>(new Err<>("Invalid RHS expression: '" + rhs + "'"));
	}

	private static Option<Result<String, String>> createErr(String msg) {
		return new Some<>(new Err<>(msg));
	}

	private static boolean isNotDeclaredCompatible(String declared, String suffix) {
		if (Objects.isNull(declared) || declared.isEmpty())
			return false;
		if (declared.startsWith("*")) {
			var baseDeclared = declared.substring(1).trim();
			var pointeeSuffix = suffix.startsWith("*") ? suffix.substring(1) : suffix;
			return !checkBasePointeeCompatibility(baseDeclared, pointeeSuffix);
		}
		return !declared.equals(suffix);
	}

	private static boolean checkBasePointeeCompatibility(String baseDeclared, String pointeeSuffix) {
		if (baseDeclared.isEmpty() || pointeeSuffix.isEmpty())
			return true;
		// Normalize and handle 'mut' prefix differences like "mut I32" vs "mut "
		var b = baseDeclared.trim();
		var p = pointeeSuffix.trim();
		if (b.startsWith("mut") && p.startsWith("mut")) {
			var bRest = b.substring(3).trim();
			var pRest = p.substring(3).trim();
			if (bRest.isEmpty() || pRest.isEmpty())
				return true;
			return bRest.equals(pRest);
		}
		return b.equals(p);
	}

	private static boolean isAssignmentStatement(String stmt) {
		// Assignment: ident = expr or compound forms like ident += expr
		if (stmt.startsWith("let "))
			return false;
		// detect compound assignment operator first (e.g., "+=")
		var plusEq = stmt.indexOf("+=");
		if (plusEq > 0) {
			var lhs = stmt.substring(0, plusEq).trim();
			return lhs.matches("[a-zA-Z_][a-zA-Z0-9_]*");
		}
		var eq = stmt.indexOf('=');
		if (eq <= 0)
			return false;
		var lhs = stmt.substring(0, eq).trim();
		// Simple identifier check - should not contain spaces or special chars except
		// ':'
		return lhs.matches("[a-zA-Z_][a-zA-Z0-9_]*");
	}

	private static Option<Result<String, String>> processAssignment(String stmt, Map<String, String[]> env) {
		// Support compound assignment like += in addition to simple '='
		var plusEq = stmt.indexOf("+=");
		boolean isCompoundPlus = plusEq > 0;
		var opPos = isCompoundPlus ? plusEq : stmt.indexOf('=');
		if (opPos <= 0)
			return createErr("Invalid assignment syntax");
		var ident = stmt.substring(0, opPos).trim();
		if (!env.containsKey(ident))
			return createErr("Unknown identifier: '" + ident + "'");
		var entry = env.get(ident);
		var mutFlag = entry[2];
		var lhsSuffix = entry[1];
		if (!isLhsAssignable(entry))
			return createErr("Assignment target is not assignable");
		var rhs = stmt.substring(opPos + (isCompoundPlus ? 2 : 1)).trim();
		var rhsEval = evaluateRhsExpression(rhs, env);
		if (!(rhsEval instanceof Some<String[]>(var pair)))
			return rhsError(rhs);
		var rhsSuffix = pair[1];
		var declaredSuffix = entry[1];
		var compatErr = validateDeclaredCompatibility(declaredSuffix, rhsSuffix);
		if (compatErr instanceof Some<Result<String, String>>)
			return compatErr;

		// Handle compound '+=' specially (extracted to helper to reduce complexity)
		if (isCompoundPlus) {
			return handleCompoundPlusAssignment(ident, entry, pair, env);
		}

		// If the LHS is a pointer variable with '*mut' suffix, update the pointee
		// instead
		if (!Objects.isNull(lhsSuffix) && lhsSuffix.startsWith("*mut")) {
			return handleDerefAssignment(entry, pair, env);
		}
		// Determine new mutability: deferred -> immutable after first assignment;
		// mutable remains mutable
		env.put(ident, makeNewEntry(pair[0], pair[1], mutFlag));
		return new None<>();
	}

	private static String[] makeNewEntry(String value, String suffix, String mutFlag) {
		var newMut = "immutable".equals(mutFlag) ? "immutable" : ("deferred".equals(mutFlag) ? "immutable" : "mutable");
		return new String[] { value, suffix, newMut };
	}

	private static Option<Result<String, String>> handleDerefAssignment(String[] pointerEntry,
			String[] rhsPair,
			Map<String, String[]> env) {
		// pointerEntry[0] holds the pointee name
		var pointeeName = pointerEntry[0];
		if (Objects.isNull(pointeeName) || pointeeName.isEmpty())
			return createErr("Pointer target not specified");
		if (!env.containsKey(pointeeName))
			return createErr("Pointer target not found: '" + pointeeName + "'");
		var pointeeEntry = env.get(pointeeName);
		// update pointee value and set its suffix to RHS suffix, keep mutability
		var updated = new String[] { rhsPair[0], rhsPair[1], pointeeEntry[2] };
		env.put(pointeeName, updated);
		return new None<>();
	}

	private static boolean isLhsAssignable(String[] entry) {
		var mutFlag = entry[2];
		var lhsSuffix = entry[1];
		// If the LHS is a pointer variable with '*mut' suffix, allow deref-assignment
		if (!Objects.isNull(lhsSuffix) && lhsSuffix.startsWith("*mut"))
			return true;
		return "mutable".equals(mutFlag) || "deferred".equals(mutFlag);
	}

	private static Option<Result<String, String>> validateDeclaredCompatibility(String declaredSuffix, String rhsSuffix) {
		if (!Objects.isNull(declaredSuffix) && !declaredSuffix.isEmpty()) {
			// If RHS has a suffix, enforce compatibility; if RHS suffix is empty, accept
			// and rely on declared type
			if (!Objects.isNull(rhsSuffix) && !rhsSuffix.isEmpty()) {
				if (isNotDeclaredCompatible(declaredSuffix, rhsSuffix))
					return new Some<>(new Err<>("Declared type does not match expression suffix"));
			}
		}
		return new None<>();
	}

	private static Result<String, String> evaluateFinal(String stmt, Map<String, String[]> env) {
		var br = evaluateBracedFinal(stmt, env);
		if (br instanceof Some<Result<String, String>>(var value)) {
			return value;
		}
		// if it's an identifier, return its value from env
		if (env.containsKey(stmt)) {
			var entry = env.get(stmt);
			// If the binding exists but has empty value and a declared suffix, treat as
			// uninitialized
			var val = entry[0];
			var declaredSuffix = entry[1];
			if ((Objects.isNull(val) || val.isEmpty()) && !Objects.isNull(declaredSuffix) && !declaredSuffix.isEmpty()) {
				return new Err<>("Uninitialized variable '" + stmt + "'");
			}
			return new Ok<>(entry[0]);
		}
		// pointer dereference expression like *y
		if (stmt.startsWith("*")) {
			var ref = stmt.substring(1).trim();
			if (!env.containsKey(ref))
				return new Err<>("Unknown pointer variable: '" + ref + "'");
			var target = env.get(ref)[0];
			if (env.containsKey(target)) {
				var entry = env.get(target);
				return new Ok<>(entry[0]);
			}
			return new Err<>("Non-empty input not allowed");
		}
		// otherwise evaluate as single expression
		return evaluateSingle(stmt);
	}

	private static Option<Result<String, String>> evaluateBracedFinal(String stmt, Map<String, String[]> env) {
		if (!stmt.startsWith("{") || matchingClosingBraceIndex(stmt) != stmt.length() - 1)
			return new None<>();
		var inner = stmt.substring(1, stmt.length() - 1).trim();
		var nonEmpty = splitNonEmptyStatements(inner);
		if (nonEmpty.isEmpty())
			return new Some<>(new Ok<>(""));
		var maybe = handleAllBindingsCase(nonEmpty, env);
		if (maybe instanceof Some<Result<String, String>>)
			return maybe;
		var buildErr = processNonFinalStatements(nonEmpty, env);
		if (buildErr instanceof Some<Result<String, String>>)
			return buildErr;
		return new Some<>(evaluateFinal(nonEmpty.getLast(), env));
	}

	private static Result<String, String> evaluateSingle(String s) {
		var resOpt = evaluateArithmeticOrLeading(s);
		if (resOpt instanceof Some<Result<String, String>>(var value)) {
			return value;
		}
		// try single-expression forms that also return a suffix (booleans, if-expr,
		// leading with suffix)
		var pairOpt = evaluateSingleWithSuffix(s);
		if (pairOpt instanceof Some<String[]>(var value)) {
			return new Ok<>(value[0]);
		}
		return new Err<>("Invalid expression: '" + s + "'");
	}

	/**
	 * Evaluate the single expression and also return the suffix of the resulting
	 * value if present. Returns Optional of String[2] where [0]=value and
	 * [1]=suffix
	 * (empty string if none). Returns empty Optional if evaluation failed.
	 */
	private static Option<String[]> evaluateSingleWithSuffix(String s) {
		// Try arithmetic or leading-digit parsing and also provide suffix
		var arithOpt = evaluateArithmeticWithSuffix(s);
		if (arithOpt instanceof Some<String[]>)
			return arithOpt;
		// leading digits case
		var leading = extractLeadingDigits(s);
		if (leading instanceof Some<String[]>(var value)) {
			return new Some<>(new String[] { value[0], value[1] });
		}
		// boolean literals
		if ("true".equals(s) || "false".equals(s)) {
			return new Some<>(new String[] { s, "" });
		}
		var ifOpt = evaluateIfWithSuffix(s);
		if (ifOpt instanceof Some<String[]>)
			return ifOpt;
		return evaluateBracedWithSuffix(s);
	}

	private static Option<String[]> evaluateIfWithSuffix(String s) {
		if (!s.startsWith("if ("))
			return new None<>();
		var close = s.indexOf(')', 4);
		if (close <= 4)
			return new None<>();
		var condExpr = s.substring(4, close).trim();
		var rest = s.substring(close + 1).trim();
		var elseIdx = rest.indexOf("else");
		if (elseIdx <= 0)
			return new None<>();
		var thenExpr = rest.substring(0, elseIdx).trim();
		var elseExpr = rest.substring(elseIdx + 4).trim();
		var condPairOpt = evaluateSingleWithSuffix(condExpr);
		if (condPairOpt instanceof Some<String[]>(var value)) {
			var condVal = value[0];
			var takeThen = "true".equals(condVal);
			var chosen = takeThen ? thenExpr : elseExpr;
			return evaluateSingleWithSuffix(chosen);
		}
		return new None<>();
	}

	private static Option<String[]> evaluateBracedWithSuffix(String s) {
		if (!s.startsWith("{") || !s.endsWith("}"))
			return new None<>();
		var inner = s.substring(1, s.length() - 1).trim();
		if (inner.isEmpty())
			return new None<>();
		if (inner.contains(";") || inner.startsWith("let ")) {
			var res = runSequence(inner);
			if (res instanceof Ok(var value)) {
				return new Some<>(new String[] { String.valueOf(value), "" });
			}
			return new None<>();
		}
		return evaluateSingleWithSuffix(inner);
	}

	private static int matchingClosingBraceIndex(String s) {
		var depth = 0;
		for (var pos = 0; pos < s.length(); pos++) {
			var ch = s.charAt(pos);
			depth = updateBraceDepth(depth, ch);
			if (isClosingBraceAtTopLevel(ch, depth)) {
				return pos;
			}
		}
		return -1;
	}

	private static boolean isClosingBraceAtTopLevel(char ch, int depth) {
		return ch == '}' && depth == 0;
	}

	// Helper that consolidates arithmetic and leading-digit handling returning
	// Optional<Result> empty when not applicable. This removes duplication detected
	// by CPD.
	private static Option<Result<String, String>> evaluateArithmeticOrLeading(String s) {
		// arithmetic case handled by shared parser
		var opOpt = parsePlusOperands(s);
		if (opOpt instanceof Some<PlusOperands>(var value)) {
			if (!value.leftSuffix.equals(value.rightSuffix)) {
				return new Some<>(new Err<>("Mismatched operand suffixes"));
			}
			return new Some<>(new Ok<>(value.sum));
		}
		// leading digits
		var leading = extractLeadingDigits(s);
		return switch (leading) {
			case None<String[]> _ -> new None<>();
			case Some<String[]>(String[] value) -> new Some<>(new Ok<>(value[0]));
		};
	}

	// Helper that evaluates arithmetic and returns Optional of [value,suffix]
	// empty when not applicable
	private static Option<String[]> evaluateArithmeticWithSuffix(String s) {
		var opOpt = parsePlusOperands(s);
		if (!(opOpt instanceof Some<PlusOperands>(var op)))
			return new None<>();
		if (!op.leftSuffix.equals(op.rightSuffix))
			return new None<>();
		return new Some<>(new String[] { op.sum, op.leftSuffix });
	}

	// Parse a plus expression like "1U8 + 2U8" and return sum and suffixs when
	// parsable
	private static Option<PlusOperands> parsePlusOperands(String s) {
		var plusIndex = s.indexOf('+');
		if (plusIndex >= 0 && plusIndex == s.lastIndexOf('+')) {
			var left = s.substring(0, plusIndex).trim();
			var right = s.substring(plusIndex + 1).trim();
			var leftNum = leadingInteger(left);
			var rightNum = leadingInteger(right);
			if (!leftNum.isEmpty() && !rightNum.isEmpty()) {
				var leftSuffix = left.substring(leftNum.length());
				var rightSuffix = right.substring(rightNum.length());
				return parseAndSumStrings(leftNum, rightNum, leftSuffix, rightSuffix);
			}
			// Try evaluating each side as a full expression (handles braced expressions)
			var leftPairOpt = evaluateSingleWithSuffix(left);
			var rightPairOpt = evaluateSingleWithSuffix(right);
			if (leftPairOpt instanceof Some<String[]>(var leftResult)) {
				if (rightPairOpt instanceof Some<String[]>(var rightResult)) {
					var leftVal = leftResult[0];
					var rightVal = rightResult[0];
					var leftSuffix = leftResult[1];
					var rightSuffix = rightResult[1];
					return parseAndSumStrings(leftVal, rightVal, leftSuffix, rightSuffix);
				}
			}
		}
		return new None<>();
	}

	private static String leadingInteger(String s) {
		if (Objects.isNull(s) || s.isEmpty()) {
			return "";
		}
		var idx = 0;
		var c = s.charAt(0);
		if (c == '+' || c == '-') {
			idx = 1;
		}
		while (idx < s.length() && Character.isDigit(s.charAt(idx))) {
			idx++;
		}
		return s.substring(0, idx);
	}

	private static Option<String[]> extractLeadingDigits(String s) {
		if (Objects.isNull(s) || s.isEmpty())
			return new None<>();
		var i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i))) {
			i++;
		}
		if (i > 0) {
			var num = s.substring(0, i);
			var suffix = s.substring(i);
			return new Some<>(new String[] { num, suffix });
		}
		return new None<>();
	}

	private static String extractIdentFromLhs(String lhs) {
		var ident = lhs;
		var colon = lhs.indexOf(':');
		if (colon > 0) {
			ident = lhs.substring(0, colon).trim();
		}
		return ident;
	}
}