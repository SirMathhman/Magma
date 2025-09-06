package magma;

import java.util.Objects;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (Objects.isNull(input)) {
			return new Err<>(new InterpretError("input is absent"));
		}
		String trimmed = input.trim();
		if (trimmed.equals("")) {
			return new Ok<>("");
		}
		// try a very small expression evaluator for binary integer ops: a <op> b
		// support + - * /
		try {
			String s = trimmed;
			int len = s.length();
			int pos = 0;
			int firstStart = pos;
			// parse first signed integer
			int firstEnd = parseSignedIntegerEnd(s, pos);
			if (firstEnd != -1) {
				pos = firstEnd;
				pos = skipWS(s, pos);
				if (pos < len) {
					char op = s.charAt(pos);
					if (op == '+' || op == '-' || op == '*' || op == '/') {
						pos++;
						pos = skipWS(s, pos);
						int secondStart = pos;
						int secondEnd = parseSignedIntegerEnd(s, pos);
						if (secondEnd != -1) {
							pos = skipWS(s, secondEnd);
							if (pos == len) {
								try {
									long a = Long.parseLong(s.substring(firstStart, firstEnd));
									long b = Long.parseLong(s.substring(secondStart, secondEnd));
									long res;
									switch (op) {
										case '+':
											res = a + b;
											break;
										case '-':
											res = a - b;
											break;
										case '*':
											res = a * b;
											break;
										case '/':
											if (b == 0) {
												return new Err<>(new InterpretError("division by zero"));
											}
											res = a / b;
											break;
										default:
											return new Ok<>(trimmed);
									}
									return new Ok<>(Long.toString(res));
								} catch (NumberFormatException ex) {
									// fall through to other parsers
								}
							}
						}
					}
				}
			}
		} catch (NumberFormatException ex) {
			// fall through to other parsers
		}
		// support a minimal let-binding form: "let x : I32 = 10; x"
		// support a minimal let-binding form: "let x : I32 = 10; x"
		try {
			String s = trimmed;
			int len = s.length();
			int pos = 0;
			// must start with 'let' followed by whitespace
			if (len >= 3 && s.startsWith("let") && (len == 3 || Character.isWhitespace(s.charAt(3)))) {
				pos = 3;
				// skip whitespace and parse identifier
				pos = skipWS(s, pos);
				int idStart = parseIdentifierStart(s, pos);
				if (idStart != -1) {
					int idEnd = parseIdentifierEnd(s, idStart);
					if (idEnd != -1) {
						String id = s.substring(idStart, idEnd);
						pos = idEnd;
						// expect ':' then I32 then '='
						int afterColon = expectCharAndSkipWS(s, pos, ':');
						if (afterColon != -1 && afterColon + 3 <= len && s.substring(afterColon, afterColon + 3).equals("I32")) {
							pos = afterColon + 3;
							pos = skipWS(s, pos);
							int afterEq = expectCharAndSkipWS(s, pos, '=');
							if (afterEq != -1) {
								int valStart = afterEq;
								if (valStart < len && (s.charAt(valStart) == '+' || s.charAt(valStart) == '-'))
									valStart++;
								int valDigitsEnd = parseUnsignedIntegerEnd(s, valStart);
								if (valDigitsEnd != -1) {
									pos = skipWS(s, valDigitsEnd);
									int afterSemicolon = expectCharAndSkipWS(s, pos, ';');
									if (afterSemicolon != -1) {
										int id2Start = afterSemicolon;
										int id2End = parseIdentifierEnd(s, id2Start);
										if (id2End != -1) {
											pos = skipWS(s, id2End);
											if (pos == len) {
												String id2 = s.substring(id2Start, id2End);
												if (id.equals(id2)) {
													String value = s.substring(valStart, valDigitsEnd);
													return new Ok<>(value);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			// on any parse error, fall through to echo
		}

		// support mutable let with assignment: "let mut x : I32 = 0; x = 10; x"
		try {
			String s = trimmed;
			int len = s.length();
			int pos = 0;
			// must start with 'let mut' followed by whitespace
			if (len >= 7 && s.startsWith("let mut") && (len == 7 || Character.isWhitespace(s.charAt(7)))) {
				pos = 7;
				pos = skipWS(s, pos);
				int idStart = parseIdentifierStart(s, pos);
				if (idStart != -1) {
					int idEnd = parseIdentifierEnd(s, idStart);
					if (idEnd != -1) {
						String id = s.substring(idStart, idEnd);
						pos = idEnd;
						int afterColon = expectCharAndSkipWS(s, pos, ':');
						if (afterColon != -1 && afterColon + 3 <= len && s.substring(afterColon, afterColon + 3).equals("I32")) {
							pos = afterColon + 3;
							pos = skipWS(s, pos);
							int afterEq = expectCharAndSkipWS(s, pos, '=');
							if (afterEq != -1) {
								// skip initial value (we don't care about it)
								int initStart = afterEq;
								if (initStart < len && (s.charAt(initStart) == '+' || s.charAt(initStart) == '-'))
									initStart++;
								int initEnd = parseUnsignedIntegerEnd(s, initStart);
								if (initEnd != -1) {
									pos = skipWS(s, initEnd);
									int afterFirstSemi = expectCharAndSkipWS(s, pos, ';');
									if (afterFirstSemi != -1) {
										// parse assignment: id = value
										int assignIdEnd = parseIdentifierEnd(s, afterFirstSemi);
										if (assignIdEnd != -1) {
											String assignId = s.substring(afterFirstSemi, assignIdEnd);
											if (id.equals(assignId)) {
												pos = assignIdEnd;
												int afterAssignEq = expectCharAndSkipWS(s, pos, '=');
												if (afterAssignEq != -1) {
													int assignStart = afterAssignEq;
													if (assignStart < len && (s.charAt(assignStart) == '+' || s.charAt(assignStart) == '-'))
														assignStart++;
													int assignEnd = parseUnsignedIntegerEnd(s, assignStart);
													if (assignEnd != -1) {
														pos = skipWS(s, assignEnd);
														int afterSecondSemi = expectCharAndSkipWS(s, pos, ';');
														if (afterSecondSemi != -1) {
															// final identifier check
															int finalIdEnd = parseIdentifierEnd(s, afterSecondSemi);
															if (finalIdEnd != -1) {
																pos = skipWS(s, finalIdEnd);
																if (pos == len) {
																	String finalId = s.substring(afterSecondSemi, finalIdEnd);
																	if (id.equals(finalId)) {
																		return new Ok<>(s.substring(assignStart, assignEnd));
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			// on any parse error, fall through to echo
		}

		// fallback: echo input
		return new Ok<>(input);
	}

	// helper: skip whitespace starting at pos, return new pos
	private static int skipWS(String s, int pos) {
		int len = s.length();
		while (pos < len && Character.isWhitespace(s.charAt(pos)))
			pos++;
		return pos;
	}

	// helper: parse a signed integer starting at pos; return end index (exclusive)
	// or -1 if none
	private static int parseSignedIntegerEnd(String s, int pos) {
		int len = s.length();
		if (pos < len && (s.charAt(pos) == '+' || s.charAt(pos) == '-'))
			pos++;
		int digitsStart = pos;
		while (pos < len && Character.isDigit(s.charAt(pos)))
			pos++;
		if (pos > digitsStart) {
			return pos;
		}
		return -1;
	}

	// helper: parse an unsigned integer starting at pos; return end index
	// (exclusive) or -1 if none
	private static int parseUnsignedIntegerEnd(String s, int pos) {
		int len = s.length();
		int start = pos;
		while (pos < len && Character.isDigit(s.charAt(pos)))
			pos++;
		if (pos > start)
			return pos;
		return -1;
	}

	// helper: return index where identifier starts (skipping whitespace) or -1
	private static int parseIdentifierStart(String s, int pos) {
		pos = skipWS(s, pos);
		int len = s.length();
		if (pos < len && (Character.isLetter(s.charAt(pos)) || s.charAt(pos) == '_'))
			return pos;
		return -1;
	}

	// helper: parse identifier (letter or underscore start) beginning at pos;
	// return end index (exclusive) or -1
	private static int parseIdentifierEnd(String s, int pos) {
		int len = s.length();
		if (pos < len && (Character.isLetter(s.charAt(pos)) || s.charAt(pos) == '_')) {
			pos++;
			while (pos < len && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_'))
				pos++;
			return pos;
		}
		return -1;
	}

	// helper: if next non-whitespace char is expected, consume it and skip
	// following whitespace; return new pos or -1
	private static int expectCharAndSkipWS(String s, int pos, char expected) {
		int len = s.length();
		pos = skipWS(s, pos);
		if (pos < len && s.charAt(pos) == expected) {
			pos++;
			return skipWS(s, pos);
		}
		return -1;
	}
}
