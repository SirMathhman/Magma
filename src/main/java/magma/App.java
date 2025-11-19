package magma;

import magma.Result.Err;
import magma.Result.Ok;
import magma.Result.Tuple;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * Core application class for Magma.
 * <p>
 * This class intentionally contains only a single public API method:
 * `interpret`.
 */
public class App {
	private record VarEntry(BigInteger value, Optional<String> resolvedType) {
	}

	private static Result<Tuple<Integer, Optional<String>>, String> parseDeclaredType(String stmt, int idx) {
		if (idx < stmt.length() && stmt.charAt(idx) == ':') {
			idx++;
			idx = skipWhitespace(stmt, idx, stmt.length());
			if (idx >= stmt.length()) {
				return new Err<Tuple<Integer, Optional<String>>, String>("invalid declaration");
			}
			var typeStart = idx;
			if (stmt.charAt(idx) != 'U' && stmt.charAt(idx) != 'I') {
				return new Err<Tuple<Integer, Optional<String>>, String>("invalid type");
			}
			idx++;
			while (idx < stmt.length() && Character.isDigit(stmt.charAt(idx))) {
				idx++;
			}
			var typeStr = stmt.substring(typeStart, idx).trim();
			if (!SUPPORTED_TYPES.contains(typeStr)) {
				return new Err<Tuple<Integer, Optional<String>>, String>("unsupported type");
			}
			idx = skipWhitespace(stmt, idx, stmt.length());
			return new Ok<Tuple<Integer, Optional<String>>, String>(new Tuple<>(idx, Optional.of(typeStr)));
		}
		return new Ok<Tuple<Integer, Optional<String>>, String>(new Tuple<>(idx, Optional.empty()));
	}

	private static Result<Tuple<BigInteger, Optional<String>>, String> evaluateAssignmentRHS(String rhs,
			Map<String, VarEntry> ctx) {
		var parsed = parseArithmeticSequence(rhs, ctx);
		if (parsed.isEmpty()) {
			return new Err<Tuple<BigInteger, Optional<String>>, String>("invalid assignment");
		}
		return parsed.get().flatMap(seq -> evaluateSequence(seq.values(), seq.operators())
				.map(value -> new Tuple<>(value, seq.resolvedType())));
	}

	private record ArithmeticSequence(List<BigInteger> values, List<Character> operators,
			Optional<String> resolvedType) {
	}

	private record OperandResult(BigInteger value, Optional<String> type, int nextCursor) {
	}

	private static final Set<String> SUPPORTED_TYPES = Set.of("U6", "U8", "U32", "U64", "I8", "I16", "I32", "I64");
	private static final Pattern OPERAND_PATTERN = Pattern.compile("^\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*");
	private static final Pattern IDENT_PATTERN = Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*");
	private static final Pattern LEADING_NUMBER_PATTERN = Pattern.compile("^(-?)(\\d+)(?:([UI])(\\d+))?");

	/**
	 * Return the leading decimal digit sequence from the provided non-null input.
	 *
	 * @param input the non-null input string
	 * @return the leading decimal digit sequence, or empty string when none exist
	 */
	public static Result<String, String> interpret(String input) {
		var ctx = new HashMap<String, VarEntry>();
		return interpretWithContext(input, ctx);
	}

	private static Result<String, String> interpretWithContext(String input, Map<String, VarEntry> ctx) {
		var statements = splitTopLevelStatements(input);
		Optional<Result<String, String>> lastOk = Optional.empty();
		for (int i = 0; i < statements.size(); i++) {
			var stmt = statements.get(i);
			var trimmed = stmt.trim();
			if (trimmed.isEmpty()) {
				continue;
			}
			var result = processStatement(new StatementContext(statements, i, trimmed, ctx));
			if (result instanceof StatementResult.Processed processed) {
				i = processed.nextIndex();
				if (processed.result().isPresent()) {
					lastOk = processed.result();
				}
				continue;
			}
			if (result instanceof StatementResult.Error error) {
				return new Err<String, String>(error.message());
			}
		}
		if (lastOk.isPresent()) {
			return lastOk.get();
		}
		return new Ok<String, String>("");
	}

	private sealed interface StatementResult {
		record Processed(int nextIndex, Optional<Result<String, String>> result) implements StatementResult {
		}

		record Error(String message) implements StatementResult {
		}
	}

	private record StatementContext(List<String> statements, int currentIndex, String trimmed, Map<String, VarEntry> ctx) {
	}

	private static StatementResult processStatement(StatementContext context) {
		var trimmed = context.trimmed();
		if (trimmed.startsWith("let") && (trimmed.length() == 3 || Character.isWhitespace(trimmed.charAt(3)))) {
			return handleLetStatement(trimmed, context.ctx(), context.currentIndex());
		}
		// Check if this is an else clause that should be combined with previous if
		if (trimmed.startsWith("else") && (trimmed.length() == 4 || Character.isWhitespace(trimmed.charAt(4)))) {
			// This else should have been handled by the previous if-expression
			// Skip it as it's already been processed
			return new StatementResult.Processed(context.currentIndex(), Optional.empty());
		}
		// Check for if-expression that might span multiple statements
		if (trimmed.startsWith("if") && (trimmed.length() == 2 || Character.isWhitespace(trimmed.charAt(2)))) {
			var ifResult = handleIfExpressionSpanning(context);
			if (ifResult.isPresent()) {
				return ifResult.get();
			}
		}
		return handleExpressionStatement(trimmed, context.ctx(), context.currentIndex());
	}

	private static StatementResult handleLetStatement(String trimmed, Map<String, VarEntry> ctx, int currentIndex) {
		var res = handleLetDeclaration(trimmed, ctx);
		if (res instanceof Err<String, String>(var error)) {
			return new StatementResult.Error(error);
		}
		return new StatementResult.Processed(currentIndex, Optional.empty());
	}

	private static StatementResult handleExpressionStatement(String trimmed, Map<String, VarEntry> ctx, int currentIndex) {
		// Check for assignment statement (x = value)
		var assignRes = handleAssignment(trimmed, ctx);
		if (assignRes.isPresent()) {
			var r = assignRes.get();
			if (r instanceof Err<String, String>(var error)) {
				return new StatementResult.Error(error);
			}
			return new StatementResult.Processed(currentIndex, Optional.empty());
		}
		var arithmeticRes = handleArithmetic(trimmed, ctx);
		if (arithmeticRes.isPresent()) {
			var r = arithmeticRes.get();
			if (r instanceof Err<String, String>(var error)) {
				return new StatementResult.Error(error);
			}
			return new StatementResult.Processed(currentIndex, Optional.of(r));
		}
		// boolean or literal or leading number logic for a single expression
		var singleRes = handleSingleExpression(trimmed, ctx);
		if (singleRes instanceof Err<String, String>(var error)) {
			return new StatementResult.Error(error);
		}
		return new StatementResult.Processed(currentIndex, Optional.of(singleRes));
	}

	private static Optional<StatementResult> handleIfExpressionSpanning(StatementContext context) {
		// Check if next statement is an else clause
		if (context.currentIndex() + 1 < context.statements().size()) {
			var nextStmt = context.statements().get(context.currentIndex() + 1).trim();
			if (nextStmt.startsWith("else") && (nextStmt.length() == 4 || Character.isWhitespace(nextStmt.charAt(4)))) {
				// Combine if and else into one statement
				var combined = context.trimmed() + " " + nextStmt;
				var ifRes = handleIfExpression(combined, context.ctx());
				if (ifRes.isPresent()) {
					var r = ifRes.get();
					if (r instanceof Err<String, String>(var error)) {
						return Optional.of(new StatementResult.Error(error));
					}
					return Optional.of(new StatementResult.Processed(context.currentIndex() + 1, Optional.of(r)));
				}
			}
		}
		return Optional.empty();
	}

	private static List<String> splitTopLevelStatements(String input) {
		var list = new ArrayList<String>();
		var start = 0;
		while (start < input.length()) {
			var semi = findTopLevel(input, start, (s, i) -> s.charAt(i) == ';');
			if (semi == -1) {
				list.add(input.substring(start));
				break;
			}
			list.add(input.substring(start, semi));
			start = semi + 1;
		}
		return list;
	}

	private static Result<String, String> handleSingleExpression(String input, Map<String, VarEntry> ctx) {
		var trimmed = input.trim();
		if ("true".equals(trimmed) || "false".equals(trimmed)) {
			return new Ok<String, String>(trimmed);
		}

		// Parse an optional sign, digits, and optional type suffix like U8 or I8.
		var m = LEADING_NUMBER_PATTERN.matcher(input);
		if (m.find()) {
			var sign = m.group(1); // "" or "-"
			var digits = m.group(2);
			var typeLetter = Optional.ofNullable(m.group(3));
			var widthStr = Optional.ofNullable(m.group(4));

			var negative = "-".equals(sign);

			if (typeLetter.isPresent() && widthStr.isPresent()) {
				var fullType = typeLetter.get() + widthStr.get();
				if (SUPPORTED_TYPES.contains(fullType)) {
					String digitsWithSign;
					if (negative) {
						digitsWithSign = "-" + digits;
					} else {
						digitsWithSign = digits;
					}
					return parseOperandValue(digitsWithSign, typeLetter, widthStr).map(BigInteger::toString);
				}
			}
			return new Ok<String, String>(digits);
		}
		return new Ok<String, String>("");
	}

	private static Result<BigInteger, String> parseOperandValue(String digitsWithSign,
			Optional<String> typeLetter,
			Optional<String> widthStr) {
		var val = new BigInteger(digitsWithSign);
		if (typeLetter.isPresent() && widthStr.isPresent()) {
			var fullType = typeLetter.get() + widthStr.get();
			if (!SUPPORTED_TYPES.contains(fullType)) {
				return new Err<BigInteger, String>("unsupported type");
			}
			return enforceNumericBound(val, fullType);
		}
		return new Ok<BigInteger, String>(val);
	}

	private static Result<BigInteger, String> enforceNumericBound(BigInteger value, String typeFull) {
		var bits = Integer.parseInt(typeFull.substring(1));
		if (typeFull.startsWith("U")) {
			if (value.signum() < 0) {
				return new Err<BigInteger, String>("negative numbers not allowed");
			}
			var max = BigInteger.ONE.shiftLeft(bits).subtract(BigInteger.ONE);
			if (value.compareTo(max) > 0) {
				return new Err<BigInteger, String>("unsigned overflow");
			}
		} else {
			var min = BigInteger.ONE.shiftLeft(bits - 1).negate();
			var max = BigInteger.ONE.shiftLeft(bits - 1).subtract(BigInteger.ONE);
			if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
				return new Err<BigInteger, String>("signed overflow");
			}
		}
		return new Ok<BigInteger, String>(value);
	}

	private static Result<String, String> enforceResultBound(BigInteger sum, String typeFull) {
		return enforceNumericBound(sum, typeFull).map(BigInteger::toString);
	}

	private record VariableAssignment(String name, BigInteger value, Optional<String> declaredType,
			Optional<String> rhsResolvedType) {
	}

	private static Result<String, String> checkTypeCompatibility(Optional<String> leftType, Optional<String> rightType) {
		if (leftType.isPresent() && rightType.isPresent() && !leftType.get().equals(rightType.get())) {
			return new Err<String, String>("operand types differ");
		}
		return new Ok<String, String>("");
	}

	private static Result<String, String> validateAndStoreVariable(VariableAssignment assignment,
			Map<String, VarEntry> ctx) {
		if (assignment.declaredType().isPresent()) {
			var boundCheck = enforceNumericBound(assignment.value(), assignment.declaredType().get());
			if (boundCheck instanceof Err<BigInteger, String>(var errVal)) {
				return new Err<String, String>(errVal);
			}
			// If RHS had a resolved type and it doesn't match declared type, error
			var typeCheck = checkTypeCompatibility(assignment.declaredType(), assignment.rhsResolvedType());
			if (typeCheck instanceof Err<String, String>(var error)) {
				return new Err<String, String>(error);
			}
			ctx.put(assignment.name(), new VarEntry(assignment.value(), assignment.declaredType()));
			return new Ok<String, String>("");
		}
		// no declared type, use RHS resolved type if present
		ctx.put(assignment.name(), new VarEntry(assignment.value(), assignment.rhsResolvedType()));
		return new Ok<String, String>("");
	}

	private static int skipWhitespace(String input, int cursor, int length) {
		while (cursor < length && Character.isWhitespace(input.charAt(cursor))) {
			cursor++;
		}
		return cursor;
	}

	private static int findMatchingParenthesis(String input, int start) {
		return findTopLevel(input, start, (s, i) -> s.charAt(i) == ')');
	}

	private static int findTopLevelEquality(String input) {
		return findTopLevel(input, 0, (s, i) -> s.charAt(i) == '=' && i + 1 < s.length() && s.charAt(i + 1) == '=');
	}

	private static Optional<Result<String, String>> handleEqualityComparison(String input, Map<String, VarEntry> ctx) {
		var splitRes = splitAtTopLevel(input, findTopLevelEquality(input), 2);
		if (splitRes.isEmpty()) {
			return Optional.empty();
		}
		var splitPair = splitRes.get();
		var left = splitPair.first();
		var right = splitPair.second();
		// boolean equality
		if (("true".equals(left) || "false".equals(left)) && ("true".equals(right) || "false".equals(right))) {
			return Optional.of(new Ok<String, String>(Boolean.toString(left.equals(right))));
		}
		// numeric equality: parse both sides as arithmetic sequences
		var leftParsed = parseArithmeticSequence(left, ctx);
		var rightParsed = parseArithmeticSequence(right, ctx);
		if (leftParsed.isEmpty() || rightParsed.isEmpty()) {
			return Optional.empty();
		}
		var leftRes = leftParsed.get();
		var rightRes = rightParsed.get();
		return Optional.of(leftRes.and(() -> rightRes).flatMap(seqPair -> {
			var lSeq = seqPair.first();
			var rSeq = seqPair.second();
			var lEval = evaluateSequence(lSeq.values(), lSeq.operators());
			var rEval = evaluateSequence(rSeq.values(), rSeq.operators());
			return lEval.flatMap(lVal -> rEval.flatMap(rVal -> {
				// If both sides have resolved types, they must match
				var typeCheck = checkTypeCompatibility(lSeq.resolvedType(), rSeq.resolvedType());
				if (typeCheck instanceof Err<String, String>(var error)) {
					return new Err<String, String>(error);
				}
				return new Ok<String, String>(Boolean.toString(lVal.equals(rVal)));
			}));
		}));
	}

	private static Optional<Result<String, String>> handleIfExpression(String input, Map<String, VarEntry> ctx) {
		var start = skipWhitespace(input, 0, input.length());
		if (start >= input.length()) {
			return Optional.empty();
		}
		if (!input.startsWith("if", start)) {
			return Optional.empty();
		}
		var afterIf = start + 2;
		var idx = skipWhitespace(input, afterIf, input.length());
		if (idx >= input.length() || input.charAt(idx) != '(') {
			return Optional.empty();
		}
		var closing = findMatchingParenthesis(input, idx);
		if (closing == -1) {
			return Optional.empty();
		}
		var cond = input.substring(idx + 1, closing).trim();
		var elseIndex = findTopLevelElse(input, closing + 1);
		if (elseIndex == -1) {
			return Optional.empty();
		}
		var thenExpr = input.substring(closing + 1, elseIndex).trim();
		var elseExpr = input.substring(elseIndex + 4).trim();
		var condEval = evaluateConditionBoolean(cond, ctx);
		if (condEval instanceof Err<Boolean, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		boolean condBool = ((Ok<Boolean, String>) condEval).value();
		Result<String, String> chosenRes;
		if (condBool) {
			chosenRes = interpretWithContext(thenExpr, ctx);
		} else {
			chosenRes = interpretWithContext(elseExpr, ctx);
		}
		if (chosenRes instanceof Err<String, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		return Optional.of(chosenRes);
	}

	private static int findTopLevelElse(String input, int from) {
		return findTopLevel(input, from, (s, i) -> s.startsWith("else", i));
	}

	private static int findTopLevel(String input, int start, BiPredicate<String, Integer> matcher) {
		var depth = 0;
		for (var i = start; i < input.length(); i++) {
			var c = input.charAt(i);
			if (c == '(') {
				depth++;
			} else if (c == ')') {
				depth--;
			}
			if (depth == 0 && matcher.test(input, i)) {
				return i;
			}
		}
		return -1;
	}

	private static Result<Boolean, String> evaluateConditionBoolean(String cond, Map<String, VarEntry> ctx) {
		var condRes = interpretWithContext(cond, ctx);
		if (condRes instanceof Err<String, String>(var error)) {
			return new Err<Boolean, String>(error);
		}
		var condVal = ((Ok<String, String>) condRes).value();
		if ("true".equals(condVal)) {
			return new Ok<Boolean, String>(true);
		}
		if ("false".equals(condVal)) {
			return new Ok<Boolean, String>(false);
		}
		try {
			var b = new BigInteger(condVal).signum() != 0;
			return new Ok<Boolean, String>(b);
		} catch (NumberFormatException nfe) {
			return new Err<Boolean, String>("invalid condition");
		}
	}

	private static boolean isOperatorChar(char op) {
		return op == '+' || op == '-' || op == '*' || op == '/';
	}

	private static Optional<Result<ArithmeticSequence, String>> parseArithmeticSequence(String input,
			Map<String, VarEntry> ctx) {
		var length = input.length();
		var cursor = 0;
		List<BigInteger> values = new ArrayList<BigInteger>();
		List<Character> operators = new ArrayList<Character>();
		Optional<String> resolvedType = Optional.empty();
		while (true) {
			cursor = skipWhitespace(input, cursor, length);
			if (cursor >= length) {
				break;
			}


			var operandResult = parseOperandAt(input, cursor, ctx);
			if (operandResult instanceof Err<Optional<OperandResult>, String>(var error)) {
				return Optional.of(new Err<ArithmeticSequence, String>(error));
			}
			var operand = ((Ok<Optional<OperandResult>, String>) operandResult).value();
			if (operand.isEmpty()) {
				return Optional.empty();
			}
			var result = operand.get();
			cursor = result.nextCursor();
			values.add(result.value());
			if (result.type().isPresent()) {
				if (resolvedType.isEmpty()) {
					resolvedType = result.type();
				} else if (!resolvedType.get().equals(result.type().get())) {
					return Optional.of(new Err<ArithmeticSequence, String>("operand types differ"));
				}
			}
			cursor = skipWhitespace(input, cursor, length);
			if (cursor >= length) {
				break;
			}
			var op = input.charAt(cursor);
			if (!isOperatorChar(op)) {
				return Optional.empty();
			}
			operators.add(op);
			cursor++;
		}
		cursor = skipWhitespace(input, cursor, length);
		if (cursor != length || values.isEmpty() || values.size() - 1 != operators.size()) {
			return Optional.empty();
		}
		return Optional.of(new Ok<ArithmeticSequence, String>(new ArithmeticSequence(values, operators, resolvedType)));
	}

	    private static Result<Optional<OperandResult>, String> parseOperandAt(String input, int cursor,
		    Map<String, VarEntry> ctx) {
		var length = input.length();
		var current = input.charAt(cursor);
		if (current == '(') {
			var closing = findMatchingParenthesis(input, cursor);
			if (closing == -1) {
				return new Ok<Optional<OperandResult>, String>(Optional.empty());
			}
			var inner = input.substring(cursor + 1, closing);
			return evaluateParenthesizedOperand(inner, closing, ctx);
		}
		// Try identifier first
		var identMatcher = IDENT_PATTERN.matcher(input);
		identMatcher.region(cursor, length);
		if (identMatcher.lookingAt()) {
			var name = identMatcher.group(1);
			if ("true".equals(name) || "false".equals(name)) {
				return new Ok<Optional<OperandResult>, String>(Optional.empty());
			}
			if (!ctx.containsKey(name)) {
				return new Err<Optional<OperandResult>, String>("unknown variable");
			}
			var entry = ctx.get(name);
			return new Ok<Optional<OperandResult>, String>(
					Optional.of(new OperandResult(entry.value(), entry.resolvedType(), identMatcher.end())));
		}
		var matcher = OPERAND_PATTERN.matcher(input);
		matcher.region(cursor, length);
		if (!matcher.lookingAt()) {
			return new Ok<Optional<OperandResult>, String>(Optional.empty());
		}
		var digitsWithSign = matcher.group(1);
		var typeLetter = Optional.ofNullable(matcher.group(2));
		var width = Optional.ofNullable(matcher.group(3));
		var operandType = typeLetter.flatMap(t -> width.map(w -> t + w));
		return parseOperandValue(digitsWithSign, typeLetter, width).map(value -> Optional.of(new OperandResult(value,
				operandType,
				matcher.end())));
	}

	private static Result<Optional<OperandResult>, String> evaluateParenthesizedOperand(String inner, int closing,
			Map<String, VarEntry> ctx) {
		var innerSeq = parseArithmeticSequence(inner, ctx);
		if (innerSeq.isEmpty()) {
			return new Ok<Optional<OperandResult>, String>(Optional.empty());
		}

		return innerSeq.get().flatMap(sequence -> evaluateSequence(sequence.values(), sequence.operators())
				.map(total -> Optional.of(new OperandResult(total, sequence.resolvedType(), closing + 1))));
	}

	private static Result<BigInteger, String> evaluateSequence(List<BigInteger> values, List<Character> operators) {
		if (values.isEmpty()) {
			return new Err<BigInteger, String>("empty sequence");
		}
		var result = values.get(0);
		for (var i = 1; i < values.size(); i++) {
			char op = operators.get(i - 1);
			var operand = values.get(i);
			if (op == '+') {
				result = result.add(operand);
			} else if (op == '-') {
				result = result.subtract(operand);
			} else if (op == '*') {
				result = result.multiply(operand);
			} else if (op == '/') {
				if (operand.signum() == 0) {
					return new Err<BigInteger, String>("division by zero");
				}
				result = result.divide(operand);
			} else {
				return new Err<BigInteger, String>("unsupported operator");
			}
		}
		return new Ok<BigInteger, String>(result);
	}

	private static Optional<Result<String, String>> handleArithmetic(String input, Map<String, VarEntry> ctx) {
		var ifRes = handleIfExpression(input, ctx);
		if (ifRes.isPresent()) {
			return ifRes;
		}
		var equality = handleEqualityComparison(input, ctx);
		if (equality.isPresent()) {
			return equality;
		}

		var parsed = parseArithmeticSequence(input, ctx);
		if (parsed.isEmpty()) {
			return Optional.empty();
		}
		return parsed.map(sequenceRes -> sequenceRes.flatMap(sequence -> evaluateSequence(sequence.values(),
				sequence.operators()).flatMap(
						total -> {
							if (sequence.resolvedType().isPresent()) {
								return enforceResultBound(total, sequence.resolvedType().get());
							}
							return new Ok<String, String>(total.toString());
						})));
	}


	private static Result<String, String> handleLetDeclaration(String stmt, Map<String, VarEntry> ctx) {
		// stmt is expected like: let x : U8 = <expr>
		var start = skipWhitespace(stmt, 3, stmt.length()); // skip 'let'
		var parseIdRes = parseDeclarationIdentifier(stmt, start);
		return parseIdRes.flatMap(idPair -> {
			var idxAfterId = skipWhitespace(stmt, idPair.first(), stmt.length());
			var name = idPair.second();
			if (ctx.containsKey(name)) {
				return new Err<String, String>("variable already declared");
			}
			var declaredTypeRes = parseDeclaredType(stmt, idxAfterId);
			return declaredTypeRes.flatMap(declaredPair -> {
				Optional<String> declaredType = declaredPair.second();
				// expect '=' at top-level
				var assignIndex = findTopLevelAssign(stmt);
				if (assignIndex < 0) {
					// Declaration without initial value - only allowed if type is specified
					if (declaredType.isEmpty()) {
						return new Err<String, String>("invalid declaration");
					}
					// Declare variable with type but no value (uninitialized)
					ctx.put(name, new VarEntry(BigInteger.ZERO, declaredType));
					return new Ok<String, String>("");
				}
				var rhs = stmt.substring(assignIndex + 1).trim();
				// evaluate RHS and return value + resolved type
				var rhsEval = evaluateAssignmentRHS(rhs, ctx);
				return rhsEval.flatMap(rhsPair -> {
					var value = rhsPair.first();
					var rhsResolvedType = rhsPair.second();
					return validateAndStoreVariable(
							new VariableAssignment(name, value, declaredType, rhsResolvedType), ctx);
				});
			});
		});
	}

	private static int findTopLevelAssign(String input) {
		return findTopLevel(input, 0, (s, i) -> s.charAt(i) == '=' && !(i + 1 < s.length() && s.charAt(i + 1) == '='));
	}

	private static Optional<Tuple<String, String>> splitAtTopLevel(String input, int index, int skipChars) {
		if (index < 0) {
			return Optional.empty();
		}
		var left = input.substring(0, index).trim();
		var right = input.substring(index + skipChars).trim();
		return Optional.of(new Tuple<>(left, right));
	}


	private static Optional<Result<String, String>> handleAssignment(String stmt, Map<String, VarEntry> ctx) {
		var splitRes = splitAtTopLevel(stmt, findTopLevelAssign(stmt), 1);
		if (splitRes.isEmpty()) {
			return Optional.empty();
		}
		var pair = splitRes.get();
		var lhs = pair.first();
		var rhs = pair.second();
		// Check if LHS is a valid identifier
		var identMatcher = IDENT_PATTERN.matcher(lhs);
		if (!identMatcher.matches()) {
			return Optional.empty();
		}
		var name = identMatcher.group(1);
		// Check if variable exists
		if (!ctx.containsKey(name)) {
			return Optional.of(new Err<String, String>("unknown variable"));
		}
		var existingEntry = ctx.get(name);
		// Evaluate RHS
		var rhsEval = evaluateAssignmentRHS(rhs, ctx);
		if (rhsEval instanceof Err<Tuple<BigInteger, Optional<String>>, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		var rhsPair = ((Ok<Tuple<BigInteger, Optional<String>>, String>) rhsEval).value();
		var value = rhsPair.first();
		var rhsResolvedType = rhsPair.second();
		// Type checking: if variable has a declared type, enforce it
		var result = validateAndStoreVariable(
				new VariableAssignment(name, value, existingEntry.resolvedType(), rhsResolvedType), ctx);
		if (result instanceof Err<String, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		return Optional.of(new Ok<String, String>(""));
	}

	private static Result<Tuple<Integer, String>, String> parseDeclarationIdentifier(String stmt, int start) {
		var m = IDENT_PATTERN.matcher(stmt);
		m.region(start, stmt.length());
		if (!m.lookingAt()) {
			return new Err<Tuple<Integer, String>, String>("invalid declaration");
		}

        
		return new Ok<Tuple<Integer, String>, String>(new Tuple<>(m.end(), m.group(1)));
	}

}