package magma;

public class StatementEvaluator {

	public static String parseEvalStmts(String t) throws InterpretException {
		if (t == null)
			return null;
		// If the string starts with a top-level brace block, strip it and continue
		String stripped = BraceUtils.stripLeadingBraceBlock(t);
		// Also support the case where the entire string is a brace block
		if (stripped.trim().startsWith("{") && stripped.trim().endsWith("}"))
			stripped = BraceUtils.stripOuterBraces(stripped);
		if (stripped.trim().isEmpty())
			return "";
		if (!stripped.contains("let") && !stripped.contains(";"))
			return null;
		return processStatementParts(stripped);
	}

	public static String stripLeadingBraceBlock(String t) {
		if (t == null)
			return null;
		String trimmed = t.trim();
		if (!trimmed.startsWith("{"))
			return t;
		int depth = 0;
		int i;
		for (i = 0; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (c == '{')
				depth++;
			else if (c == '}') {
				depth--;
				if (depth == 0) {
					i++;
					break;
				}
			}
		}
		if (depth != 0)
			return t; // unmatched braces — leave as is
		// if the leading brace block covers the whole trimmed string, don't strip it
		// here
		if (i >= trimmed.length())
			return t;
		// return the remainder after the leading brace block
		return trimmed.substring(i).trim();
	}

	private static String processStatementParts(String t) throws InterpretException {
		String[] parts = t.split(";");
		StatementContext ctx = new StatementContext();
		for (int i = 0; i < parts.length; i++) {
			String stmt = parts[i].trim();
			// strip any leading top-level brace blocks from this statement part
			String normalized = BraceUtils.stripLeadingBraceBlock(stmt);
			// repeat in case there are multiple leading blocks like "{} {} x"
			while (!normalized.equals(stmt)) {
				stmt = normalized;
				normalized = BraceUtils.stripLeadingBraceBlock(stmt);
			}
			stmt = stmt.trim();
			if (stmt.isEmpty())
				continue;
			Long maybeValue = null;
			try {
				maybeValue = evaluateStatement(stmt, ctx);
			} catch (IllegalArgumentException ex) {
				throw new InterpretException(ex.getMessage());
			}
			if (maybeValue == null)
				continue;
			if (!anyLaterPart(parts, i))
				return String.valueOf(maybeValue.longValue());
		}
		return null;
	}

	private static boolean anyLaterPart(String[] parts, int i) {
		for (int k = i + 1; k < parts.length; k++)
			if (!parts[k].trim().isEmpty())
				return true;
		return false;
	}

	private static Long evaluateStatement(String stmt, StatementContext ctx) {
		if (stmt.startsWith("let")) {
			if (!parseLetStatement(stmt, ctx))
				throw new IllegalArgumentException("Invalid let");
			return null;
		}
		int eq = stmt.indexOf('=');
		if (eq >= 0) {
			String lhs = stmt.substring(0, eq).trim();
			String rhs = stmt.substring(eq + 1).trim();
			if (lhs.startsWith("*"))
				return handleDerefAssignment(lhs, rhs, ctx);
			return handleNormalAssignment(lhs, rhs, ctx);
		}
		return Long.valueOf(evaluateExprWithEnv(stmt, ctx.env, ctx.refs));
	}

	private static Long handleDerefAssignment(String lhs, String rhs, StatementContext ctx) {
		String targetName = lhs.substring(1).trim();
		IdentifierUtils.IdParseResult tid = IdentifierUtils.parseIdentifier(targetName, 0);
		if (tid == null || tid.next != targetName.length())
			throw new IllegalArgumentException("Invalid deref lhs");
		String pointee = ctx.refs.get(tid.name);
		if (pointee == null)
			throw new IllegalArgumentException("Not a pointer");
		Boolean ptrMutable = ctx.refsMutable.get(tid.name);
		if (ptrMutable == null || !ptrMutable)
			throw new IllegalArgumentException("Pointer not mutable");
		long val = evaluateExprWithEnv(rhs, ctx.env, ctx.refs);
		ctx.env.put(pointee, val);
		return null;
	}

	private static Long handleNormalAssignment(String lhs, String rhs, StatementContext ctx) {
		IdentifierUtils.IdParseResult id = IdentifierUtils.parseIdentifier(lhs, 0);
		if (id == null || id.next != lhs.length())
			throw new IllegalArgumentException("Invalid assignment");
		String name = id.name;
		Boolean isMutable = ctx.mut.get(name);
		if (isMutable == null || !isMutable)
			throw new IllegalArgumentException("Not mutable");
		String rhsTrim = rhs.trim();
		if (rhsTrim.startsWith("&"))
			return handleAddressOfAssignment(name, rhsTrim, ctx);
		long val = evaluateExprWithEnv(rhs, ctx.env, ctx.refs);
		ctx.env.put(name, val);
		return null;
	}

	private static Long handleAddressOfAssignment(String name, String rhsTrim, StatementContext ctx) {
		String target = rhsTrim.substring(1).trim();
		boolean isMutAddr = false;
		if (target.startsWith("mut ")) {
			isMutAddr = true;
			target = target.substring(4).trim();
		}
		IdentifierUtils.IdParseResult tid = IdentifierUtils.parseIdentifier(target, 0);
		if (tid == null || tid.next != target.length())
			throw new IllegalArgumentException("Invalid address-of");
		if (!ctx.env.containsKey(tid.name))
			throw new IllegalArgumentException("Unknown var");
		ctx.refs.put(name, tid.name);
		ctx.refsMutable.put(name, isMutAddr);
		ctx.env.put(name, 0L);
		return null;
	}

	private static long evaluateExprWithEnv(String expr, java.util.Map<String, Long> env,
			java.util.Map<String, String> refs) {
		ExpressionParser p = new ExpressionParser(expr);
		long v = p.parseExprRes(new ExpressionParser.VarResolver() {
			public long resolve(String name) {
				Long val = env.get(name);
				if (val == null)
					throw new IllegalArgumentException("Unknown var");
				return val.longValue();
			}

			public long resolveRef(String name) {
				String target = refs.get(name);
				if (target == null)
					throw new IllegalArgumentException("Not a pointer");
				Long val = env.get(target);
				if (val == null)
					throw new IllegalArgumentException("Unknown var");
				return val.longValue();
			}
		});
		p.skipWhitespace();
		if (!p.isAtEnd())
			throw new IllegalArgumentException("Trailing data");
		return v;
	}

	private static boolean parseLetStatement(String stmt, StatementContext ctx) {
		java.util.Map<String, Long> env = ctx.env;
		java.util.Map<String, Boolean> mut = ctx.mut;
		java.util.Map<String, String> types = ctx.types;
		java.util.Map<String, String> refs = ctx.refs;
		String after = stmt.substring(3).trim();
		// split on '=' first to separate LHS and RHS
		int eq = after.indexOf('=');
		String lhs;
		String rhs = null;
		if (eq < 0) {
			lhs = after.trim();
		} else {
			lhs = after.substring(0, eq).trim();
			rhs = after.substring(eq + 1).trim();
			if (rhs.isEmpty())
				return false;
		}
		// lhs should be like: [mut] <ident> or [mut] <ident> : <type>
		String[] lhsParts = lhs.split(":");
		String ident = lhsParts[0].trim();
		boolean isMutable = false;
		if (ident.startsWith("mut ")) {
			isMutable = true;
			ident = ident.substring(4).trim();
		}
		IdentifierUtils.IdParseResult id = IdentifierUtils.parseIdentifier(ident, 0);
		if (id == null || id.next != ident.length())
			return false;
		String name = id.name;
		// redeclaration not allowed
		if (env.containsKey(name))
			return false;
		boolean ptrTypeMutable = parseTypeForLet(lhsParts, types);
		try {
			long val;
			if (rhs == null) {
				// no initializer — only allowed for mutable declarations
				if (!isMutable)
					return false;
				val = 0L;
			} else {
				// handle address-of initializer like &x
				String rhsTrim = rhs.trim();
				if (rhsTrim.startsWith("&")) {
					java.util.AbstractMap.SimpleEntry<String, Boolean> res = handleAddrInit(rhsTrim, ptrTypeMutable, ctx);
					if (res == null)
						return false;
					ctx.refs.put(name, res.getKey());
					ctx.refsMutable.put(name, res.getValue());
					val = 0L;
				} else {
					val = evaluateExprWithEnv(rhs, env, refs);
				}
			}
			env.put(name, val);
			mut.put(name, isMutable);
			// store type if provided
			if (lhsParts.length > 1) {
				types.put(name, lhsParts[1].trim());
			}
			return true;
		} catch (RuntimeException ex) {
			return false;
		}
	}

	private static boolean parseTypeForLet(String[] lhsParts, java.util.Map<String, String> types) {
		if (lhsParts.length <= 1)
			return false;
		String type = lhsParts[1].trim();
		if (type.startsWith("*")) {
			String inner = type.substring(1).trim();
			if (inner.startsWith("mut ")) {
				inner = inner.substring(4).trim();
			}
			return isAllowedSuffix(inner);
		} else {
			return isAllowedSuffix(type);
		}
	}

	private static java.util.AbstractMap.SimpleEntry<String, Boolean> handleAddrInit(String rhsTrim,
			boolean ptrTypeMutable, StatementContext ctx) {
		String target = rhsTrim.substring(1).trim();
		boolean isMutAddr = false;
		if (target.startsWith("mut ")) {
			isMutAddr = true;
			target = target.substring(4).trim();
		}
		IdentifierUtils.IdParseResult tid = IdentifierUtils.parseIdentifier(target, 0);
		if (tid == null || tid.next != target.length())
			return null;
		if (!ctx.env.containsKey(tid.name))
			return null;
		return new java.util.AbstractMap.SimpleEntry<>(tid.name, (ptrTypeMutable || isMutAddr));
	}

	private static boolean isAllowedSuffix(String s) {
		if (s == null)
			return false;
		switch (s) {
			case "U8":
			case "U16":
			case "U32":
			case "U64":
			case "I8":
			case "I16":
			case "I32":
			case "I64":
				return true;
			default:
				return false;
		}
	}

}
