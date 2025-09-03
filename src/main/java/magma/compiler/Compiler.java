package magma.compiler;

import magma.ast.SeqItem;
import magma.ast.StmtSeq;
import magma.ast.Structs;
import magma.ast.Unit;
import magma.ast.VarDecl;
import magma.diagnostics.CompileError;
import magma.emit.CEmitter;
import magma.emit.JsEmitter;
import magma.parser.ParseResult;
import magma.parser.Parser;
import magma.parser.ParserUtils;
import magma.util.Err;
import magma.util.Ok;
import magma.util.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

public class Compiler {
	// Return start index of a standalone token, or -1 if not found.
	// (moved to CompilerUtil)
	// Delegate struct handling to helper
	public final Structs structs = new Structs();

	// Return true if s is a braced numeric literal like `{5}` (allow whitespace).
	// (moved numeric helpers to CompilerUtil)

	// Return true if s is a plain numeric literal like `0`, `5`, `123` (allow
	// whitespace).
	// (moved numeric helpers to CompilerUtil)
	// Collect extra global functions (impls) registered during parsing so CEmitter
	// can emit them before main.
	public final Collection<String> extraGlobalFunctions = new ArrayList<>();
	// Simple enum registry: enum name -> list of members
	public final Map<String, List<String>> enums = new HashMap<>();
	// Simple type alias registry: alias -> target type (e.g. Simple -> I32)
	final Map<String, String> typeAliases = new HashMap<>();
	// Impl methods registry: struct name -> method name -> JS function expression
	public final Map<String, Map<String, String>> implMethods = new HashMap<>();
	// Impl method bodies for C rewrite: method name -> body expr using `this`
	final Map<String, String> implMethodBodies = new HashMap<>();
	// C emitter helper: function variable return types (name -> C type)
	public final Map<String, String> fnReturnTypesC = new HashMap<>();
	/**
	 * Central compiler implementation for Magma.
	 * <p>
	 * if (provided != expected) {
	 * return new Err<>(new CompileError("Struct initializer for '" + sl.name() + "'
	 * expects " + expected + " values, got " + provided));
	 * }
	 * // check types for each provided value using current decls
	 * java.util.List<String> expectedTypes = this.structs.getFieldTypes(sl.name());
	 * for (int vi = 0; vi < provided; vi++) {
	 * String valExpr = sl.vals().get(vi).trim();
	 * String actual = magma.compiler.Semantic.exprType(this, valExpr, decls);
	 * String exp = vi < expectedTypes.size() ? expectedTypes.get(vi) : null;
	 * if (exp != null && actual != null && !exp.equals(actual)) {
	 * return new Err<>(new CompileError("Struct initializer type mismatch for '" +
	 * sl.name() + "' field '" + sl.fields().get(vi) + "'"));
	 * }
	 * }
	 * - Helpers have been extracted to keep this class focused:
	 * - Parser utilities -> `ParserUtils`
	 * - Token/top-level helpers and numeric checks -> `CompilerUtil`
	 * - Prefer small, focused, top-level helper classes (for example
	 * `CompilerUtil`). Avoid inner (non-static nested) and local classes to
	 * simplify testing and avoid classloading/visibility issues.
	 * - PMD/CPD thresholds are intentionally conservative; prefer small,
	 * readable refactorings over mass removal of duplicated blocks.
	 * - Configuration files:
	 * CheckStyle: config/checkstyle/checkstyle.xml
	 * PMD/CPD: config/pmd/
	 * - Run tests locally before and after substantive changes:
	 * mvn -q -DskipTests=false clean test
	 */
	private final String target;
	// Counter for anonymous structs created to represent `this` in C
	public int anonStructCounter = 0;

	public Compiler(String targetLanguage) {
		this.target = Optional.ofNullable(targetLanguage).map(t -> t.toLowerCase(Locale.ROOT)).orElse("");
	}

	// Replace dotted enum accesses like Name.Member with Name_Member in the
	// provided
	// expression and return the replaced string.
	public Optional<String> replaceEnumDotAccess(String expr) {
		return Optional.ofNullable(expr).map(e -> {
			var out = e;
			for (var en : this.enums.entrySet()) {
				var ename = en.getKey();
				for (var mem : en.getValue()) {
					var dotted = ename + "." + mem;
					var repl = ename + "_" + mem;
					out = out.replace(dotted, repl);
				}
			}
			return out;
		});
	}

	// Emit simple C #defines for enums: NAME_MEMBER as increasing integers.
	public String emitEnumDefinesC() {
		var sb = new StringBuilder();
		for (var e : this.enums.entrySet()) {
			var ename = e.getKey();
			var idx = 0;
			for (var mem : e.getValue()) {
				sb.append("#define ").append(ename).append("_").append(mem).append(" ").append(idx).append("\n");
				idx++;
			}
		}
		return sb.toString();
	}

	// ...existing code...

	// Helper to consume trailing remainder after a braceEnd index in a top-level
	// declaration string. Returns the remainder trimmed, or Optional.empty() if nothing left.
	private static Optional<String> consumeTrailingRemainder(String p, int braceEnd) {
		var remainder = p.substring(braceEnd).trim();
		if (!remainder.isEmpty() && ';' == remainder.charAt(0))
			remainder = remainder.substring(1).trim();
		return remainder.isEmpty() ? Optional.empty() : Optional.of(remainder);
	}

	// (moved findBracedRegion to CompilerUtil to reduce Compiler method count)

	// Extract inner content between '{' at index braceIdx and its matching '}' at
	// braceEnd (inclusive). Returns trimmed inner content or Optional.empty() if invalid.
	private static Optional<String> innerBetweenBracesAt(String p, int braceIdx, int braceEnd) {
		if (0 > braceIdx || braceEnd <= braceIdx || braceEnd > p.length())
			return Optional.empty();
		// original code used substring(brace+1, braceEnd - 1); adjust to
		// inclusive/exclusive
		return Optional.of(p.substring(braceIdx + 1, braceEnd - 1).trim());
	}

	// Advance from position p (starting after an opening '(') until matching
	// Advance from position p (starting after an opening '(') until matching
	// closing parenthesis is found. Returns index of the character after the
	// closing ')', or -1 if unmatched.
	public int advanceNested(String s, int p) {
		return this.advanceNestedGeneric(s, p, '(', ')');
	}

	// Returns: 0 = none found, 1 = valid call found (readInt()),
	// 2 = bare identifier found (invalid), 3 = call with arguments (invalid).
	int findReadIntUsage(String src) {
		var key = "readInt";
		var idx = 0;
		var foundCall = false;
		while (true) {
			var end = CompilerUtil.findStandaloneTokenEnd(src, key, idx);
			if (-1 == end)
				break;
			var j = CompilerUtil.skipWhitespace(src, end);
			if (j < src.length() && '(' == src.charAt(j)) {
				// find matching ')'
				var p = this.advanceNested(src, j + 1);
				if (-1 == p) {
					// unbalanced parens -> treat as invalid
					return 3;
				}
				var contentStart = j + 1;
				var contentEnd = p - 1; // exclusive
				var hasNonWs = false;
				for (var k = contentStart; k < contentEnd; k++) {
					if (!Character.isWhitespace(src.charAt(k))) {
						hasNonWs = true;
						break;
					}
				}
				if (hasNonWs)
					return 3; // call with args -> invalid
				foundCall = true;
				idx = p; // continue searching after ')'
			} else {
				return 2; // bare identifier — invalid
			}
		}
		return foundCall ? 1 : 0;
	}

	public Result<Set<Unit>, CompileError> compile(Iterable<Unit> units) {
		Set<Unit> out = new HashSet<>();
		for (var u : units) {
			var src = null == u.input() ? "" : u.input();
			var expr = this.extractExpression(src);

			// parse statements to detect duplicate variable declarations and analyze each
			// part
			var prCheckRes = this.parseStatements(expr);
			if (prCheckRes instanceof Err<?, ?>) {
				var error = ((Err<ParseResult, CompileError>) prCheckRes).error();
				return new Err<>(error);
			}
			var prCheck = ((Ok<ParseResult, CompileError>) prCheckRes).value();

			// detect invalid calls on non-identifiers (e.g. `5()`)
			for (var st : prCheck.stmts()) {
				var e = Semantic.detectNonIdentifierCall(null == st ? "" : st);
				if (null != e)
					return e;
			}
			var eFinal = Semantic.detectNonIdentifierCall(null == prCheck.last() ? "" : prCheck.last());
			if (null != eFinal)
				return eFinal;

			// Validate struct initializer argument types for declarations (extra safety)
			for (var d : prCheck.decls()) {
				var rhs = null == d.rhs() ? "" : d.rhs().trim();
				if (!rhs.isEmpty()) {
					var sl = this.structs.parseStructLiteral(rhs);
					if (null != sl) {
						var ce = Semantic.validateStructLiteral(this, sl, prCheck.decls());
						if (null != ce)
							return new Err<>(ce);
					}
				}
			}

			Collection<String> seen = new HashSet<>();
			var wantsReadInt = false;
			for (var d : prCheck.decls()) {
				if (!seen.add(d.name())) {
					return new Err<>(new CompileError("Duplicate variable: " + d.name()));
				}
				// If this declaration is a function, ensure no duplicate parameter names
				if (null != d.type() && d.type().contains("=>")) {
					var inner = CompilerUtil.getParamsInnerTypeSegment(d.type());
					if (null != inner) {
						Collection<String> pnames = new HashSet<>();
						var depth = 0;
						var start = 0;
						for (var i = 0; i <= inner.length(); i++) {
							var atEnd = i == inner.length();
							var c = atEnd ? ',' : inner.charAt(i);
							if ('(' == c)
								depth++;
							else if (')' == c)
								depth--;
							if ((',' == c && 0 == depth) || atEnd) {
								var part = inner.substring(start, i).trim();
								if (!part.isEmpty()) {
									var colon = part.indexOf(':');
									var pname = -1 == colon ? part.trim() : part.substring(0, colon).trim();
									if (!pnames.add(pname)) {
										return new Err<>(new CompileError("Duplicate parameter: " + pname));
									}
								}
								start = i + 1;
							}
						}
					}
				}
				var rhs = null == d.rhs() ? "" : d.rhs().trim();
				if ("readInt".equals(rhs)) {
					// bare readInt allowed only if declared type is a function type (contains =>)
					var declType = this.dTypeOf(d);
					if (null == declType || !declType.contains("=>")) {
						return new Err<>(new CompileError("Invalid use of readInt"));
					}
					wantsReadInt = true;
				} else {
					var usageRhs = this.findReadIntUsage(rhs);
					if (2 == usageRhs)
						return new Err<>(new CompileError("Bare 'readInt' used in initializer for variable '" + d.name() + "'"));
					if (3 == usageRhs)
						return new Err<>(
								new CompileError("'readInt' called with arguments in initializer for variable '" + d.name() + "'"));
					if (1 == usageRhs)
						wantsReadInt = true;
				}
				// If declaration has an explicit non-function type and an initializer, check
				// type matches inferred rhs
				var declType = null == d.type() ? "" : d.type().trim();
				if (isABoolean(declType, rhs)) {
					// If RHS is a struct literal, treat its type as the struct name
					var trimmedRhs = rhs.trim();
					var slRhs = this.structs.parseStructLiteral(trimmedRhs);
					String actual = null;
					if (null != slRhs) {
						actual = slRhs.name();
					} else {
						actual = Semantic.exprType(this, rhs, prCheck.decls());
					}
					if ("Simple".equals(declType)) {
						System.err.println(
								"DEBUG declType='" + declType + "', aliasValue='" + this.typeAliases.get(declType) + "', allAliases=" +
										this.typeAliases);
					}
					// resolve declared type via aliases (follow chains)
					var resolvedDeclType = declType;
					while (this.typeAliases.containsKey(resolvedDeclType)) {
						resolvedDeclType = this.typeAliases.get(resolvedDeclType);
					}
					if (null != actual) {
						if (resolvedDeclType.contains("|")) {
							var ok = false;
							for (var part : resolvedDeclType.split("\\|")) {
								var p = part.trim();
								// follow alias chain for part
								var partResolved = p;
								while (this.typeAliases.containsKey(partResolved)) {
									partResolved = this.typeAliases.get(partResolved);
								}
								if (actual.equals(partResolved)) {
									ok = true;
									break;
								}
							}
							if (!ok) {
								// Fallback: try splitting the raw declared type (in case aliases
								// were not registered at parse time) and compare parts directly.
								for (var part2 : declType.split("\\|")) {
									if (actual.equals(part2.trim())) {
										ok = true;
										break;
									}
								}
							}
							if (!ok)
								return new Err<>(new CompileError(
										"Initializer type mismatch for variable '" + d.name() + "' (actual=" + actual + ", expected in=" +
												resolvedDeclType + ", declType=" + declType + ", alias=" + this.typeAliases.get(declType)
												+ ")"));
						} else {
							if (!actual.equals(resolvedDeclType)) {
								// Allow pointer mutability differences: treat '*mut T' and '*T' as compatible
								if (actual.startsWith("*") && resolvedDeclType.startsWith("*")) {
									var aNorm = actual.replace("*mut ", "*").replace("* ", "*").trim();
									var dNorm = resolvedDeclType.replace("*mut ", "*").replace("* ", "*").trim();
									if (aNorm.equals(dNorm)) {
										// compatible, continue
									} else {
										return new Err<>(new CompileError(
												"Initializer type mismatch for variable '" + d.name() + "' (actual=" + actual + ", expected=" +
														resolvedDeclType + ")"));
									}
								} else {
									return new Err<>(new CompileError(
											"Initializer type mismatch for variable '" + d.name() + "' (actual=" + actual + ", expected=" +
													resolvedDeclType + ")"));
								}
							}
						}
					}
				}
			}
			// If any declaration uses a braced numeric initializer like `{5}`, set
			// wantsReadInt so the JS/C helpers are emitted (tests expect this legacy
			// behaviour where braces indicate reading input in tests).
			for (var d : prCheck.decls()) {
				if (CompilerUtil.isBracedNumeric(d.rhs())) {
					wantsReadInt = true;
					break;
				}
			}

				// Detect multiple mutable borrows (&mut X) in the same declaration list
				// e.g. `let mut x = 0; let y = &mut x; let z = &mut x;` should be invalid
				var mutBorrowCounts = new java.util.HashMap<String, Integer>();
				for (var d : prCheck.decls()) {
					if (d.rhs() == null) continue;
					var r = d.rhs().trim();
					if (r.startsWith("&mut ")) {
						var target = r.substring(5).trim();
						var cnt = mutBorrowCounts.getOrDefault(target, 0) + 1;
						mutBorrowCounts.put(target, cnt);
						if (cnt > 1) {
							return new Err<>(new CompileError("Multiple mutable borrows of '" + target + "'"));
						}
					}
				}

			// Check for scope violations: variables declared in braced blocks should not be
			// accessible outside
			var lastExpr = null == prCheck.last() ? "" : prCheck.last().trim();
			if (!lastExpr.isEmpty() && lastExpr.matches("[A-Za-z_][A-Za-z0-9_]*")) {
				// Check if the final expression is a simple identifier that might be declared
				// in a braced block
				for (Object seqItem : prCheck.seq()) {
					if (seqItem instanceof String stmt) {
						if (!stmt.trim().isEmpty() && '{' == stmt.trim().charAt(0) && !stmt.trim().isEmpty() &&
								'}' == stmt.trim().charAt(stmt.trim().length() - 1)) {
							// This is a braced block statement
							var bracedContent = stmt.trim();
							bracedContent = bracedContent.substring(1, bracedContent.length() - 1).trim();
							// Check if this braced content declares the variable referenced in lastExpr
							if (bracedContent.contains("let " + lastExpr + " ") || bracedContent.contains("let " + lastExpr + "=") ||
									bracedContent.contains("let " + lastExpr + ":") || bracedContent.contains("let " + lastExpr + ";")) {
								return new Err<>(
										new CompileError("Variable '" + lastExpr + "' declared in braced block is not accessible outside"));
							}
						}
					}
				}
			}

			// check final expression
			var finalExpr = null == prCheck.last() ? "" : prCheck.last();
			var arErrFinal = Semantic.validateFunctionCallArity(this, finalExpr, prCheck.decls());
			if (null != arErrFinal)
				return arErrFinal;
			var finalUsage = this.findReadIntUsage(finalExpr);
			if (2 == finalUsage) {
				return new Err<>(new CompileError("Bare 'readInt' used as final expression"));
			}
			if (3 == finalUsage) {
				return new Err<>(new CompileError("'readInt' called with arguments in final expression"));
			}
			if (1 == finalUsage)
				wantsReadInt = true;

			// if final expression is an if-expression, ensure the condition is boolean
			var ifParts = Semantic.parseIfExpression(this, finalExpr);
			if (null != ifParts) {
				var cond = ifParts[0];
				if (!this.exprLooksBoolean(cond)) {
					return new Err<>(new CompileError("If condition must be boolean"));
				}
			}

			// check non-let statements (e.g., assignments) for readInt usage
			Map<String, Boolean> assigned = new HashMap<>();
			for (var vd : prCheck.decls()) {
				assigned.put(vd.name(), null != vd.rhs() && !vd.rhs().isEmpty());
			}
			for (var si = 0; si < prCheck.stmts().size();) {
				var s = prCheck.stmts().get(si);
				var usageStmt = this.findReadIntUsage(null == s ? "" : s);
				// If this is an 'if' followed by an 'else' statement, handle both together
				var sTrim = null == s ? "" : s.trim();
				if (sTrim.startsWith("if ") && si + 1 < prCheck.stmts().size()) {
					var next = prCheck.stmts().get(si + 1);
					var nextTrim = null == next ? "" : next.trim();
					if (nextTrim.startsWith("else")) {
						// combined if-else
						var parts = Semantic.parseIfExpression(this, s + "; " + nextTrim);
						// parseIfExpression expects a single if...else string; if it fails, fallback
						if (null != parts) {
							var thenExpr = parts[1];
							var elseExpr = parts[2];
							// check readInt usage in condition and both branches
							var useCond = this.findReadIntUsage(null == parts[0] ? "" : parts[0]);
							var useThen = this.findReadIntUsage(null == thenExpr ? "" : thenExpr);
							var useElse = this.findReadIntUsage(null == elseExpr ? "" : elseExpr);
							if (1 == useCond || 1 == useThen || 1 == useElse) {
								wantsReadInt = true;
							}
							if (2 == useCond || 2 == useThen || 2 == useElse) {
								return new Err<>(new CompileError("Bare 'readInt' used in statement: '" + s + "'"));
							}
							if (3 == useCond || 3 == useThen || 3 == useElse) {
								return new Err<>(new CompileError("'readInt' called with arguments in statement: '" + s + "'"));
							}

							var lhsThen = CompilerUtil.getAssignmentLhs(thenExpr);

							var lhsElse = CompilerUtil.getAssignmentLhs(elseExpr);
							var err0 = CompilerUtil.handleThenElseAssignment(this, lhsThen, lhsElse, prCheck.decls(), assigned);
							if (null != err0)
								return err0;
							si += 2;
							continue;
						}
					}
				}
				// default: single statement handling
				var left = CompilerUtil.getAssignmentLhs(s);

				if (null != left) {
					// Disallow assignments to struct fields (dotted access like `p.x = ...`).
					// Detect if there's a '.' before the top-level '=' in the statement.
					var eqPos = CompilerUtil.findTopLevelOp(s, "=");
					if (-1 != eqPos) {
						var dotPos = s.indexOf('.');
						if (-1 != dotPos && dotPos < eqPos) {
							return new Err<>(new CompileError("Assignment to struct field: " + s.substring(0, eqPos).trim()));
						}
					}
					// If this is a compound assignment or increment/decrement, ensure the
					// target variable is numeric (I32 or initialized from readInt or braced
					// numeric).
					if (CompilerUtil.isCompoundOrIncrement(s)) {
						VarDecl targetCheck = null;
						for (var vd : prCheck.decls()) {
							if (vd.name().equals(left)) {
								targetCheck = vd;
								break;
							}
						}
						if (null != targetCheck) {
							var numeric = false;
							var dt = this.dTypeOf(targetCheck);
							if (dt.equals("I32"))
								numeric = true;
							if (!numeric) {
								// check initializer for readInt() call or braced numeric or plain numeric
								var usage = this.findReadIntUsage(null == targetCheck.rhs() ? "" : targetCheck.rhs());
								if (1 == usage || CompilerUtil.isBracedNumeric(targetCheck.rhs()) ||
										CompilerUtil.isPlainNumeric(targetCheck.rhs()))
									numeric = true;
							}
							if (!numeric) {
								return new Err<>(new CompileError("Compound assignment on non-numeric variable '" + left + "'"));
							}
						}
					}
					var ident = left.matches("[A-Za-z_][A-Za-z0-9_]*");
					if (ident) {
						VarDecl target = null;
						for (var vd : prCheck.decls()) {
							if (vd.name().equals(left)) {
								target = vd;
								break;
							}
						}
						if (null == target) {
							return new Err<>(new CompileError("Assignment to undefined variable '" + left + "'"));
						}
						boolean wasAssigned = assigned.getOrDefault(target.name(), false);

						// Detect dereference assignment like '*y = ...' by checking char before
						// identifier
						var pos = s.indexOf(left);
						var derefAssign = false;
						if (pos > 0) {
							var k = pos - 1;
							while (k >= 0 && Character.isWhitespace(s.charAt(k)))
								k--;
							if (k >= 0 && s.charAt(k) == '*')
								derefAssign = true;
						}

						if (derefAssign) {
							// If this is a deref assignment, allow it only if the pointee variable is
							// mutable.
							String pointeeName = null;
							if (target.rhs() != null) {
								var r = target.rhs().trim();
								if (r.startsWith("&mut ")) {
									pointeeName = r.substring(5).trim();
								} else if (r.startsWith("&")) {
									pointeeName = r.substring(1).trim();
								}
							}
							if (pointeeName != null) {
								VarDecl pointee = null;
								for (var vd2 : prCheck.decls()) {
									if (vd2.name().equals(pointeeName)) {
										pointee = vd2;
										break;
									}
								}
								if (pointee == null) {
									return new Err<>(
											new CompileError("Assignment to undefined variable '" + pointeeName + "' via pointer"));
								}
								if (!pointee.mut()) {
									return new Err<>(new CompileError("Assignment to immutable variable '" + pointeeName + "'"));
								}
								assigned.put(pointee.name(), true);
							} else {
								// conservative fallback: disallow when pointee unknown
								return new Err<>(new CompileError("Assignment through pointer to unknown target '" + left + "'"));
							}

						} else {
							if (target.mut()) {
								assigned.put(target.name(), true);
							} else {
								if (wasAssigned) {
									return new Err<>(new CompileError("Assignment to immutable variable '" + left + "'"));
								}
								assigned.put(target.name(), true);
							}
						}
					}
				}
				if (2 == usageStmt) {
					return new Err<>(new CompileError("Bare 'readInt' used in statement: '" + s + "'"));
				}
				if (3 == usageStmt) {
					return new Err<>(new CompileError("'readInt' called with arguments in statement: '" + s + "'"));
				}
				if (1 == usageStmt)
					wantsReadInt = true;
				si++;
			}

			// Also ensure the final expression is not an assignment to a struct field
			var finalExprCheck = null == prCheck.last() ? "" : prCheck.last();
			var finalEq = CompilerUtil.findTopLevelOp(finalExprCheck, "=");
			if (-1 != finalEq) {
				var dotPos = finalExprCheck.indexOf('.');
				if (-1 != dotPos && dotPos < finalEq) {
					return new Err<>(
							new CompileError("Assignment to struct field: " + finalExprCheck.substring(0, finalEq).trim()));
				}
			}

			// Ensure every declaration without initializer is assigned later in stmts.
			for (var vd : prCheck.decls()) {
				if (null == vd.rhs() || vd.rhs().isEmpty()) {
					var declAssigned = false;
					for (var s : prCheck.stmts()) {
						if (this.isAssignmentTo(s, vd.name())) {
							declAssigned = true;
							break;
						}
					}
					if (!declAssigned) {
						return new Err<>(
								new CompileError("Variable '" + vd.name() + "' declared without initializer or assignment"));
					}
				}
			}

			if ("typescript".equals(this.target)) {
				var js = new StringBuilder();
				// include readInt helper only when needed
				if (wantsReadInt) {
					js.append("const fs = require('fs');\n");
					js.append("const inRaw = fs.readFileSync(0, 'utf8');\n");
					js.append("const tokens = (inRaw.match(/\\S+/g) || []);\n");
					js.append("let __idx = 0;\n");
					js.append("function readInt(){ return parseInt(tokens[__idx++] || '0'); }\n");
				}
				var jsRes = this.buildJsExpression(expr);
				if (jsRes instanceof Err<?, ?>) {
					var error = ((Err<String, CompileError>) jsRes).error();
					return new Err<>(error);
				}
				var jsExpr = ((Ok<String, CompileError>) jsRes).value();
				if (null != jsExpr && !jsExpr.isEmpty()) {
					js.append("console.log(").append(jsExpr).append(");\n");
				}
				out.add(new Unit(u.location(), ".js", js.toString()));
			} else if ("c".equals(this.target)) {
				var c = new StringBuilder();
				c.append("#include <stdio.h>\n");
				c.append("#include <stdlib.h>\n");
				var cRes = this.buildCParts(expr);
				if (cRes instanceof Err<?, ?>) {
					var error = ((Err<String[], CompileError>) cRes).error();
					return new Err<>(error);
				}
				var cParts = ((Ok<String[], CompileError>) cRes).value();
				// include readInt helper only when needed
				if (wantsReadInt) {
					c.append("int readInt(){ int x; if (scanf(\"%d\", &x)==1) return x; return 0; }\n");
				}
				var globalDefs = null == cParts[0] ? "" : cParts[0];
				// include any extra global functions hoisted during parsing
				if (!this.extraGlobalFunctions.isEmpty()) {
					var eg = new StringBuilder();
					for (var s : this.extraGlobalFunctions) {
						eg.append(s).append("\n");
					}
					globalDefs = eg.toString() + globalDefs;
				}
				var prefix = null == cParts[1] ? "" : cParts[1];
				var exprC = 2 < cParts.length && null != cParts[2] ? cParts[2] : "";
				// emit any global function definitions before main
				if (!globalDefs.isEmpty()) {
					c.append(globalDefs);
				}
				if (exprC.isEmpty()) {
					c.append("int main() { return 0; }");
				} else {
					var looksBoolean = this.exprLooksBoolean(exprC);

					// if expr is a simple identifier and declared as Bool, treat as boolean
					if (!looksBoolean) {
						var id = null == exprC ? "" : exprC.trim();
						// If expression is a simple identifier, check declared type
						if (id.matches("[A-Za-z_][A-Za-z0-9_]*")) {
							for (var vd : prCheck.decls()) {
								if (vd.name().equals(id)) {
									var dt = this.dTypeOf(vd);
									if (dt.equals("Bool")) {
										looksBoolean = true;
										break;
									}
								}
							}
						} else {
							// If expression is an indexing expression like ident[...], treat as the
							// element's type based on the declared array type for the identifier.
							var m = id.matches("([A-Za-z_][A-Za-z0-9_]*)\\[.*\\]") ? id : null;
							if (m != null) {
								// extract identifier before '['
								var br = id.indexOf('[');
								var baseId = id.substring(0, br).trim();
								for (var vd : prCheck.decls()) {
									if (vd.name().equals(baseId)) {
										var dt = this.dTypeOf(vd);
										// If no explicit declared type, try to infer from the initializer
										if ((dt == null || dt.isEmpty()) && vd.rhs() != null && !vd.rhs().trim().isEmpty()) {
											var inferred = magma.compiler.Semantic.exprType(this, vd.rhs(), prCheck.decls());
											if (inferred != null && !inferred.isEmpty()) dt = inferred;
										}
										if (dt != null && dt.startsWith("[")) {
											var elem = magma.parser.ParserUtils.arrayElementType(dt);
											if ("Bool".equals(elem)) {
											looksBoolean = true;
										}
										break;
									}
									}
								}
							}
						}
					}
					if (-1 != CompilerUtil.findStandaloneTokenIndex(exprC, "true", 0) ||
							-1 != CompilerUtil.findStandaloneTokenIndex(exprC, "false", 0) ||
							-1 != CompilerUtil.findStandaloneTokenIndex(prefix, "true", 0) ||
							-1 != CompilerUtil.findStandaloneTokenIndex(prefix, "false", 0)) {
						c.insert(0, "#include <stdbool.h>\n");
					}
					if (prefix.isEmpty()) {
						if (looksBoolean) {
							c.append("int main() { printf(\"%s\", (").append(exprC).append(") ? \"true\" : \"false\"); return 0; }");
						} else {
							c.append("int main() { int res = ").append(exprC).append("; printf(\"%d\", res); return 0; }");
						}
					} else {
						if (looksBoolean) {
							c.append("int main() { ")
									.append(prefix)
									.append(" printf(\"%s\", (")
									.append(exprC)
									.append(") ? \"true\" : \"false\"); return 0; }");
						} else {
							c.append("int main() { ")
									.append(prefix)
									.append(" int res = ")
									.append(exprC)
									.append("; printf(\"%d\", res); return 0; }");
						}
					}
				}
				out.add(new Unit(u.location(), ".c", c.toString()));
			} else {
				out.add(u);
			}
		}
		return new Ok<>(out);
	}

	private boolean isABoolean(String declType, String rhs) {
		return !declType.isEmpty() && !declType.contains("=>") && null != rhs && !rhs.isEmpty();
	}

	// Helper: validate assignment to `name` using declarations list and assigned
	// map.
	// Returns magma.Err<magma.CompileError> if invalid, otherwise null and marks
	// the var as
	// assigned.
	private Err<Set<Unit>, CompileError> checkAndMarkAssignment(String name,
			Iterable<VarDecl> decls,
			Map<String, Boolean> assigned) {
		VarDecl target = null;
		for (var vd : decls) {
			if (vd.name().equals(name)) {
				target = vd;
				break;
			}
		}
		return null;
	}

	// Remove the prelude declaration if present and trim; used to get the
	// expression to evaluate.
	private String extractExpression(String src) {
		if (null == src)
			return "";
		// Recognize the intrinsic prelude declaration and remove it from the source
		var out = src;
		var prelude = "intrinsic fn readInt() : I32;";
		var idx = out.indexOf(prelude);
		if (-1 != idx) {
			out = out.substring(0, idx) + out.substring(idx + prelude.length());
		}
		out = out.trim();
		// remove trailing semicolon if present
		if (!out.isEmpty() && ';' == out.charAt(out.length() - 1))
			out = out.substring(0, out.length() - 1).trim();
		// If the whole expression is wrapped in braces { ... }, strip one layer
		if (2 <= out.length() && '{' == out.charAt(0) && '}' == out.charAt(out.length() - 1)) {
			var after = this.advanceNestedGeneric(out, 1, '{', '}');
			if (after == out.length()) {
				out = out.substring(1, out.length() - 1).trim();
			}
		}
		if (out.isEmpty())
			return "";
		return out;
	}

	// Generic nested-advance helper used by the specialized methods above.
	public int advanceNestedGeneric(String s, int p, char openChar, char closeChar) {
		return ParserUtils.advanceNested(s, p, openChar, closeChar);
	}

	// If `src` is a single braced block like "{...}" (with balanced braces),
	// return the inner content trimmed, otherwise return the original src.
	public String unwrapBraced(String src) {
		if (null == src)
			return null;
		var t = src.trim();
		if (2 <= t.length() && '{' == t.charAt(0) && '}' == t.charAt(t.length() - 1)) {
			var after = this.advanceNestedGeneric(t, 1, '{', '}');
			if (after == t.length())
				return t.substring(1, t.length() - 1).trim();
		}
		return src;
	}

	// Convert simple language constructs into a JS expression string.
	// Supports optional leading 'let' declarations followed by an expression,
	// separated by semicolons.
	// (moved to CompilerUtil)

	// moved ensureReturnInBracedBlock implementation to
	// Parser.ensureReturnInBracedBlock

	// Normalize an arrow RHS for JS: strip param types, convert ternary, and
	// if the body is a braced multi-statement block convert it into an expression
	// or IIFE as appropriate.
	public String normalizeArrowRhsForJs(String rhs) {
		return Parser.normalizeArrowRhsForJs(this, rhs);
	}

	// appendDecl moved to CompilerUtil to keep method count low

	// (intentionally left blank; helper removed to avoid lastSeen capture)

	// Convert a leading if-expression `if (cond) then else elseExpr` into a JS
	// ternary expression using the centralized parse helper. Recurses into
	// branches to handle nested ifs.
	public String convertLeadingIfToTernary(String src) {
		var parts = Semantic.parseIfExpression(this, src);
		if (null == parts)
			return null == src ? "" : src;
		parts[1] = this.convertLeadingIfToTernary(parts[1]);
		parts[2] = this.convertLeadingIfToTernary(parts[2]);
		return "((" + parts[0] + ") ? (" + parts[1] + ") : (" + parts[2] + "))";
	}

	// Convert simple language constructs into a JS expression string.
	private Result<String, CompileError> buildJsExpression(String exprSrc) {
		var parsedRes = this.parseStatements(exprSrc);
		if (parsedRes instanceof Err<?, ?>) {
			return new Err<>(((Err<ParseResult, CompileError>) parsedRes).error());
		}
		var parsedResult = ((Ok<ParseResult, CompileError>) parsedRes).value();
		var prPrefix = JsEmitter.renderSeqPrefix(this, parsedResult);
		var last = parsedResult.last();
		last = this.convertLeadingIfToTernary(last);
		last = Parser.ensureReturnInBracedBlock(this, last, false);
		// convert `is` operator (e.g. `value is I32`) into JS-friendly checks
		last = IsOperatorProcessor.convertForJs(this, last, parsedResult);
		if (prPrefix.isEmpty())
			return new Ok<>(JsEmitter.rewriteDerefInExpr(last));
		return new Ok<>("(function(){ " + prPrefix + " return (" + JsEmitter.rewriteDerefInExpr(last) + "); })()");
	}

	// For C we need to return triple: global function defs, prefix statements (in
	// main), and the final expression.
	private Result<String[], CompileError> buildCParts(String exprSrc) {
		var prRes = this.parseStatements(exprSrc);
		if (prRes instanceof Err<?, ?>) {
			return new Err<>(((Err<ParseResult, CompileError>) prRes).error());
		}
		var pr = ((Ok<ParseResult, CompileError>) prRes).value();
		var cparts = CEmitter.renderSeqPrefixC(this, pr);
		var globalDefs = cparts[0];
		var prefix = cparts[1];
		var expr = null == pr.last() ? "" : pr.last();
		// Rewrite simple zero-arg method calls on known structs: obj.name()
		// into the recorded body with `this` replaced by obj.
		if (null != expr) {
			var dot = expr.indexOf('.');
			var paren = expr.indexOf('(');
			if (0 < dot && paren > dot && !expr.isEmpty() && ')' == expr.charAt(expr.length() - 1)) {
				var obj = expr.substring(0, dot).trim();
				var mname = expr.substring(dot + 1, paren).trim();
				var keyAny = "." + mname; // search by suffix across impl types
				String body = null;
				for (var e : this.implMethodBodies.entrySet()) {
					if (e.getKey().endsWith(keyAny)) {
						body = e.getValue();
						break;
					}
				}
				if (null != body) {
					var replaced = body.replace("this", obj);
					expr = replaced;
				}
			}
		}
		expr = this.convertLeadingIfToTernary(expr);
		// convert `is` operator for C using declaration info
		expr = IsOperatorProcessor.convertForC(this, expr, pr);
		// translate dotted enum accesses like Name.Member to Name_Member for C
		if (null != expr) {
			expr = this.replaceEnumDotAccess(expr).orElse("");
			prefix = this.replaceEnumDotAccess(prefix).orElse("");
		}
		return new Ok<>(new String[] { globalDefs, prefix, expr });
	}

	// `is` operator processing moved to IsOperatorProcessor helper

	// Helper that returns the ParseResult or sets the provided out-array with
	// a CompileError when parsing fails. This avoids using exceptions and lets
	// callers return appropriate Result types.

	// Convert a param list like "(x : I32, y : I32)" into C params "(int x, int
	// y)".
	// (moved to CompilerUtil)

	// Build a struct literal string for C or JS. For C, produce a compound literal
	// like `(Name){ .f = v, ... }`. For JS, produce an object literal like
	// `{ f: v, ... }`.
	// struct literal helpers are delegated to `structs` helper to avoid code
	// duplication

	// var-decl emission helpers moved to JsEmitter and CEmitter

	// Remove type annotations from a parameter list like "(x : I32, y : I32)"
	// without using regular expressions.
	// (moved to CompilerUtil)

	// Centralized parsing of simple semicolon-separated statements into var decls
	// and final expression. Returns Result.ok(ParseResult) or Err(CompileError).
	private Result<ParseResult, CompileError> parseStatements(String exprSrc) {
		var parts = Parser.splitByChar(exprSrc);
		// ...existing code...
		List<VarDecl> decls = new ArrayList<>();
		List<String> stmts = new ArrayList<>();
		List<SeqItem> seq = new ArrayList<>();
		var last = "";
		for (var p : parts) {
			p = p.trim();
			if (p.isEmpty())
				continue;

			// detect simple type alias: `type Name = Target`
			if (p.startsWith("type ")) {
				var rest = p.substring(5).trim();
				var eq = rest.indexOf('=');
				if (-1 == eq) {
					// invalid type declaration
					return new Err<>(new CompileError("Invalid type declaration: " + p));
				}
				var name = rest.substring(0, eq).trim();
				var val = rest.substring(eq + 1).trim();
				// remove trailing semicolon if present
				if (!val.isEmpty() && ';' == val.charAt(val.length() - 1))
					val = val.substring(0, val.length() - 1).trim();
				if (name.isEmpty() || val.isEmpty())
					return new Err<>(new CompileError("Invalid type declaration: " + p));
				// alias name must start with uppercase letter (A-Z)
				char first = name.charAt(0);
				if (!('A' <= first && 'Z' >= first))
					return new Err<>(new CompileError("Invalid type declaration: " + p));
				this.typeAliases.put(name, val);
				// continue to next part
				continue;
			}
			// detect one or more consecutive struct declarations: `struct Name { ... }`
			while (p.startsWith("struct ")) {
				var nameStart = 7;
				var brace = p.indexOf('{', nameStart);
				if (-1 == brace)
					break;
				var structBraceEnd = this.advanceNestedGeneric(p, brace + 1, '{', '}');
				if (-1 == structBraceEnd)
					break;
				// struct-specific field parsing
				var name = p.substring(nameStart, brace).trim();
				var innerOpt = Compiler.innerBetweenBracesAt(p, brace, structBraceEnd);
				if (innerOpt.isEmpty()) {
					return new Err<>(new CompileError("Invalid struct body: " + p));
				}
				var inner = innerOpt.get();
				// split fields by commas or semicolons
				var fparts = Semantic.splitTopLevel(inner, ',', '{', '}');
				List<String> fields = new ArrayList<>();
				List<String> types = new ArrayList<>();
				Collection<String> seenFields = new HashSet<>();
				for (var fp : fparts) {
					var fpTrim = fp.trim();
					if (fpTrim.isEmpty())
						continue;
					var colon = fpTrim.indexOf(':');
					var fname = -1 == colon ? fpTrim : fpTrim.substring(0, colon).trim();
					var ftype = -1 == colon ? "I32" : fpTrim.substring(colon + 1).trim();
					if (!fname.isEmpty()) {
						if (!seenFields.add(fname)) {
							return new Err<>(new CompileError("Duplicate struct member: " + fname));
						}
						fields.add(fname);
						types.add(ftype.isEmpty() ? "I32" : ftype);
					}
				}
				if (fields.isEmpty()) {
					return new Err<>(new CompileError("Empty struct: " + name));
				}
				var err = this.structs.registerWithTypes(name, fields, types);
				if (err.isPresent()) {
					return new Err<>(err.get());
				}
				// process any trailing remainder after this struct; if none, we're done
				var remainderOpt = Compiler.consumeTrailingRemainder(p, structBraceEnd);
				if (remainderOpt.isEmpty()) {
					p = "";
					break;
				}
				var remainder = remainderOpt.get();
				// set p to remainder and loop to detect another struct
				p = remainder;
			}
			// If the remainder begins with a type declaration (e.g. "type X = ...")
			// register it now so subsequent checks in this iteration see the alias.
			if (null != p && p.startsWith("type ")) {
				var rest2 = p.substring(5).trim();
				var eq2 = rest2.indexOf('=');
				if (-1 == eq2) {
					return new Err<>(new CompileError("Invalid type declaration: " + p));
				}
				var name2 = rest2.substring(0, eq2).trim();
				var val2 = rest2.substring(eq2 + 1).trim();
				if (!val2.isEmpty() && ';' == val2.charAt(val2.length() - 1))
					val2 = val2.substring(0, val2.length() - 1).trim();
				if (name2.isEmpty() || val2.isEmpty())
					return new Err<>(new CompileError("Invalid type declaration: " + p));
				// alias name must start with uppercase letter (A-Z)
				char first2 = name2.charAt(0);
				if (!('A' <= first2 && 'Z' >= first2))
					return new Err<>(new CompileError("Invalid type declaration: " + p));
				this.typeAliases.put(name2, val2);
				// treat remainder after registering type as processed
				continue;
			}
			// detect enum declaration: `enum Name { ... }` — treat like struct for parsing
			if (p.startsWith("enum ")) {
				var nameStart = 5;
				var regionObj = CompilerUtil.findBracedRegion(p, nameStart);
				if (null != regionObj) {
					int[] region = regionObj;
					var brace = region[0];
					var braceEnd = region[1];
					var innerOpt = Compiler.innerBetweenBracesAt(p, brace, braceEnd);
					var inner = innerOpt.isPresent() ? innerOpt.get() : "";
					List<String> members = new ArrayList<>();
					for (var part : Semantic.splitTopLevel(inner, ',', '{', '}')) {
						var t = part.trim();
						if (!t.isEmpty()) {
							var semi = t.indexOf(';');
							if (-1 != semi)
								t = t.substring(0, semi).trim();
							members.add(t);
						}
					}
					var name = p.substring(nameStart, brace).trim();
					if (!name.isEmpty()) {
						this.enums.put(name, members);
					}
					var regionObj2 = CompilerUtil.findBracedRegion(p, nameStart);
					if (regionObj2 == null)
						continue;
					int[] region2 = regionObj2;
					var braceEnd2 = region2[1];
					var remainderOpt = Compiler.consumeTrailingRemainder(p, braceEnd2);
					if (remainderOpt.isEmpty())
						continue;
					p = remainderOpt.get();
				}
			}
			// detect impl declaration: `impl Name { ... }` — register methods (for JS) and
			// consume it
			if (p.startsWith("impl ")) {
				var nameStart = 5;
				var regionObj3 = CompilerUtil.findBracedRegion(p, nameStart);
				if (null == regionObj3) {
					p = "";
					continue;
				}
				int[] region3 = regionObj3;
				var braceStart3 = region3[0];
				var braceEnd3 = region3[1];
				// Extract struct name for impl
				var typeName = p.substring(nameStart, braceStart3).trim();
				// Parse inner methods and record as function expressions for JS
				var innerOpt2 = Compiler.innerBetweenBracesAt(p, braceStart3, braceEnd3);
				if (innerOpt2.isPresent() && !innerOpt2.get().isEmpty()) {
					var stmtsInImpl = Semantic.splitTopLevel(innerOpt2.get(), ';', '{', '}');
					for (var s2 : stmtsInImpl) {
						var t2 = s2 == null ? "" : s2.trim();
						if (t2.startsWith("fn ")) {
							CompilerUtil.registerImplFn(this, typeName, t2);
						}
					}
				}
				var rem2Opt = Compiler.consumeTrailingRemainder(p, braceEnd3);
				if (rem2Opt.isEmpty()) {
					p = "";
					continue;
				}
				p = rem2Opt.get();
			}
			// detect class fn declaration(s): `class fn Name(params) => body` — register as
			// fn returning `this`
			while (p.startsWith("class fn ")) {
				var stripped = "fn " + p.substring("class fn ".length());
				var fnParts = Parser.parseFnDeclaration(this, stripped);
				if (null == fnParts) {
					last = Parser.handleStatementProcessing(this, p, stmts, seq);
					break;
				} else {
					var name = fnParts[0];
					var params = fnParts[1];
					var retType = fnParts[2];
					var remainder = 4 < fnParts.length ? fnParts[4] : "";
					// Use the provided body, but ensure braced bodies return a value
					// so the class factory returns an object (this). This preserves
					// any inner `fn` declarations inside the class body.
					var body = fnParts[3];
					String rhsBody;
					if (null != body && !body.trim().isEmpty() && '{' == body.trim().charAt(0)) {
						// Extract nested fn declarations and register them as impl methods
						// so emitters can attach them to constructed objects. Keep any
						// remaining statements as the factory body.
						var innerRaw = body.trim().substring(1, body.trim().length() - 1);
						var partsInner = Semantic.splitTopLevel(innerRaw, ';', '{', '}');
						var remaining = new StringBuilder();
						for (var s2 : partsInner) {
							var t2 = null == s2 ? "" : s2.trim();
							if (t2.isEmpty())
								continue;
							if (t2.startsWith("fn ")) {
								var fparts = Parser.parseFnDeclaration(this, t2);
								if (null != fparts) {
									var mname = fparts[0];
									var mparams = fparts[1];
									var mbody = fparts[3];
									var paramsClean = CompilerUtil.stripParamTypes(mparams);
									String funcExpr;
									if (null != mbody && !mbody.trim().isEmpty() && '{' == mbody.trim().charAt(0)) {
										var ensured = Parser.ensureReturnInBracedBlock(this, mbody, false, mparams);
										funcExpr = "function" + paramsClean + " " + ensured;
									} else {
										var expr = this.unwrapBraced(mbody);
										funcExpr = "function" + paramsClean + " { return " + expr + "; }";
									}
									var map = this.implMethods.get(name);
									if (null == map) {
										map = new HashMap<>();
										this.implMethods.put(name, map);
									}
									map.put(mname, funcExpr);
									if ("()".equals(paramsClean)) {
										var bodyExpr = (null != mbody && !mbody.trim().isEmpty() && '{' == mbody.trim().charAt(0))
												? Parser.ensureReturnInBracedBlock(this, mbody,
														true, "")
												: this.unwrapBraced(mbody);
										this.implMethodBodies.put(name + "." + mname, bodyExpr);
									}
									// don't include this fn in the remaining body
									continue;
								}
							}
							if (0 < remaining.length())
								remaining.append("; ");
							remaining.append(t2);
						}
						if (0 == remaining.length()) {
							rhsBody = "{ this }";
						} else {
							rhsBody = "{" + remaining + " }";
						}
					} else {
						rhsBody = this.unwrapBraced(body);
					}
					var rhs = params + " => " + (null == rhsBody ? "{ this }" : rhsBody);
					var type = params + " => " + (null == retType || retType.isEmpty() ? "I32" : retType);
					CompilerUtil.appendDecl(decls, seq, new VarDecl(name, rhs, type, false));
					if (null != remainder) {
						var rtrim = remainder.trim();
						if (!rtrim.isEmpty()) {
							p = rtrim;
						} else {
							last = name;
							p = "";
							break;
						}
					} else {
						last = name;
						p = "";
						break;
					}
				}
			}
			if (p.startsWith("let ")) {
				// find assignment '=' that is not inside parentheses and not part of '=='
				var eq = -1;
				var depthEq = 0;
				for (var i = 4; i < p.length(); i++) {
					var ch = p.charAt(i);
					if ('(' == ch)
						depthEq++;
					else if (')' == ch)
						depthEq--;
					else if ('=' == ch && 0 == depthEq) {
						// skip '==' operator and '=>' arrow in types
						if (i + 1 < p.length()) {
							var next = p.charAt(i + 1);
							if ('=' == next || '>' == next)
								continue;
						}
						eq = i;
						break;
					}
				}
				String left;
				String rhs;
				// allow declarations without initializer: `let x : I32;`
				if (-1 == eq) {
					left = p.substring(4).trim();
					rhs = "";
				} else {
					left = p.substring(4, eq).trim();
					rhs = p.substring(eq + 1).trim();
				}
				// optional 'mut' after let
				var isMut = false;
				if (left.startsWith("mut ")) {
					isMut = true;
					left = left.substring(4).trim();
				}
				var colon = left.indexOf(':');
				var name = -1 == colon ? left.trim() : left.substring(0, colon).trim();
				var type = -1 == colon ? "" : left.substring(colon + 1).trim();
				var vd = new VarDecl(name, rhs, type, isMut);
				// If RHS is a struct literal, ensure the number of values matches the struct's
				// fields
				if (!rhs.isEmpty()) {
					var sl = this.structs.parseStructLiteral(rhs.trim());
					if (null != sl) {
						var ce = Semantic.validateStructLiteral(this, sl, decls);
						if (null != ce)
							return new Err<>(ce);
					}
				}
				decls.add(vd);
				seq.add(vd);
				last = name;
			} else if (p.startsWith("fn ")) {
				// Parse function declaration: fn name(params) : Return => expr
				var fnParts = Parser.parseFnDeclaration(this, p);
				if (null == fnParts) {
					// Invalid syntax, treat as regular statement
					last = Parser.handleStatementProcessing(this, p, stmts, seq);
				} else {
					var name = fnParts[0];
					var params = fnParts[1];
					var retType = fnParts[2];
					var body = fnParts[3];
					var remainder = 4 < fnParts.length ? fnParts[4] : "";

					// Create as a function variable declaration
					// Type will be params => returnType (if provided) else default to I32
					var type = params + " => " + (null == retType || retType.isEmpty() ? "I32" : retType);

					// If the body is just a function call like "readInt()",
					// assign the function itself for C compatibility
					String rhs;
					if (body.matches("\\w+\\(\\)")) {
						// Extract function name from "functionName()"
						rhs = body.substring(0, body.indexOf('(')); // Just the function name for C compatibility
					} else {
						rhs = params + " => " + body; // Arrow function for other cases
					}

					CompilerUtil.appendDecl(decls, seq, new VarDecl(name, rhs, type, false));
					if (null != remainder) {
						var rem2 = remainder.trim();
						if (!rem2.isEmpty()) {
							stmts.add(rem2);
							seq.add(new StmtSeq(rem2));
							last = rem2;
						} else {
							last = name;
						}
					} else {
						last = name;
					}
				}
			} else {
				// Check if this statement contains a while loop followed by an expression
				last = Parser.handleStatementProcessing(this, p, stmts, seq);
			}
		}
		// If the last non-let statement is the final expression, don't include it in
		// stmts
		if (!stmts.isEmpty() && last.equals(stmts.getLast())) {
			// remove the final element by index to support ArrayList
			stmts.removeLast();
			// also remove the trailing element from the ordered seq so we don't emit it
			if (!seq.isEmpty()) {
				var lastSeq = seq.getLast();
				if (lastSeq instanceof StmtSeq) {
					var stmt = ((StmtSeq) lastSeq).stmt();
					if (last.equals(stmt)) {
						seq.removeLast();
					}
				}
			}
		}

		return new Ok<>(new ParseResult(decls, stmts, last, seq));
	}

	String dTypeOf(VarDecl d) {
		return null == d ? null : d.type();
	}

	// Token-aware boolean detection: looks for standalone true/false or '==' token
	private boolean exprLooksBoolean(String s) {
		if (null == s || s.isEmpty())
			return false;
		var t = s.trim();
		// remove surrounding parentheses pairs to expose top-level ternary
		var changed = true;
		while (changed && 2 <= t.length() && '(' == t.charAt(0) && ')' == t.charAt(t.length() - 1)) {
			changed = false;
			// If the matching closing paren for the opening at index 0 is at the end,
			// strip the outer pair.
			var after = this.advanceNested(t, 1);
			if (after == t.length()) {
				t = t.substring(1, t.length() - 1).trim();
				changed = true;
			}
		}
		// If it's a ternary expression, inspect both branches by finding a top-level
		// '?'
		var qIdx = -1;
		var search = 0;
		while (true) {
			search = t.indexOf('?', search);
			if (-1 == search)
				break;
			if (CompilerUtil.isTopLevelPos(t, search)) {
				qIdx = search;
				break;
			}
			search += 1;
		}
		if (-1 != qIdx) {
			var colon = -1;
			var s2 = qIdx + 1;
			while (true) {
				s2 = t.indexOf(':', s2);
				if (-1 == s2)
					break;
				if (CompilerUtil.isTopLevelPos(t, s2)) {
					colon = s2;
					break;
				}
				s2 += 1;
			}
			if (-1 != colon) {
				var thenPart = t.substring(qIdx + 1, colon).trim();
				var elsePart = t.substring(colon + 1).trim();
				return this.exprLooksBoolean(thenPart) && this.exprLooksBoolean(elsePart);
			}
		}

		if (-1 != CompilerUtil.findStandaloneTokenIndex(t, "true", 0))
			return true;
		if (-1 != CompilerUtil.findStandaloneTokenIndex(t, "false", 0))
			return true;
		// find top-level '==' occurrences — treat as boolean
		var idx = 0;
		while (true) {
			idx = t.indexOf("==", idx);
			if (-1 == idx)
				break;
			if (CompilerUtil.isTopLevelPos(t, idx))
				return true;
			idx += 2;
		}
		// detect relational operators (<, >, <=, >=, !=) as boolean
		var relOps = new String[] { "<=", ">=", "!=", "<", ">" };
		for (var op : relOps) {
			var id = 0;
			while (true) {
				id = t.indexOf(op, id);
				if (-1 == id)
					break;
				// ensure operator is not adjacent to identifier characters
				if (0 < id) {
					var prev = t.charAt(id - 1);
					if (Character.isLetterOrDigit(prev) || '_' == prev) {
						id += op.length();
						continue;
					}
				}
				var after = id + op.length();
				if (after < t.length()) {
					var next = t.charAt(after);
					if (Character.isLetterOrDigit(next) || '_' == next) {
						id += op.length();
						continue;
					}
				}
				return true;
			}
		}
		return false;
	}

	// (identifier helper moved to CompilerUtil)

	// Return true if statement `stmt` is an assignment whose LHS is exactly
	// varName.
	private boolean isAssignmentTo(String stmt, String varName) {
		var lhs = CompilerUtil.getAssignmentLhs(stmt);
		return null != lhs && lhs.equals(varName);
	}

	// Return the LHS identifier of a simple assignment statement `name = ...`,
	// or null if the statement is not an assignment.
	// (moved to CompilerUtil)

	// Scan left from index j (inclusive) for an identifier and return it, or
	// null if none found. Skips whitespace before the identifier.
	// (moved to CompilerUtil)

	// (top-level operator helpers moved to CompilerUtil)

	// (removed validateReadIntUsage) use findReadIntUsage directly for contextual
	// errors
	// Parsing and semantic helpers are now in nested classes (Parser/Semantic)
}
