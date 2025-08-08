package magma;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

final class StatementHandlers {
	private StatementHandlers() {}

	static boolean handleLetInt(String stmt,
															Map<String, String> types,
															Map<String, Boolean> mutable,
															List<String> outputs) {
		Matcher m = Compiler.LET_INT_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		String cType = Compiler.toCType(m.group(2), m.group(3));
		types.put(m.group(1), cType);
		mutable.put(m.group(1), Boolean.FALSE);
		outputs.add(cType + " " + m.group(1) + " = 0;");
		return true;
	}

	static boolean handleLetTypedLiteral(String stmt,
																			 Map<String, String> types,
																			 Map<String, Boolean> mutable,
																			 List<String> outputs) {
		Matcher m = Compiler.LET_TYPED_LITERAL_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		String cType = Compiler.toCType(m.group(3), m.group(4));
		types.put(m.group(1), cType);
		mutable.put(m.group(1), Boolean.FALSE);
		outputs.add(cType + " " + m.group(1) + " = " + m.group(2) + ";");
		return true;
	}

	static boolean handleLetDefaultI32(String stmt,
																		 Map<String, String> types,
																		 Map<String, Boolean> mutable,
																		 List<String> outputs) {
		Matcher m = Compiler.LET_DEFAULT_I32_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		types.put(m.group(1), "int32_t");
		mutable.put(m.group(1), Boolean.FALSE);
		outputs.add("int32_t " + m.group(1) + " = " + m.group(2) + ";");
		return true;
	}

	static boolean handleLetFromIdentifier(String stmt,
																				 Map<String, String> types,
																				 Map<String, Boolean> mutable,
																				 List<String> outputs) throws CompileException {
		Matcher m = Compiler.LET_FROM_IDENTIFIER_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		String rhsType = types.get(m.group(2));
		if (rhsType == null) throw new CompileException();
		types.put(m.group(1), rhsType);
		mutable.put(m.group(1), Boolean.FALSE);
		outputs.add(rhsType + " " + m.group(1) + " = " + m.group(2) + ";");
		return true;
	}

	static boolean handleLetConditional(String stmt,
																			Map<String, String> types,
																			Map<String, Boolean> mutable,
																			List<String> outputs) throws CompileException {
		Matcher m = Compiler.LET_CONDITIONAL_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		String cond = m.group(2), thenExpr = m.group(3), elseExpr = m.group(4);
		Compiler.validateCondition(cond, types);
		String t1 = Compiler.resolveExprType(thenExpr, types);
		String t2 = Compiler.resolveExprType(elseExpr, types);
		if (!t1.equals(t2)) throw new CompileException();
		types.put(m.group(1), t1);
		mutable.put(m.group(1), Boolean.FALSE);
		outputs.add(t1 + " " + m.group(1) + " = " + cond + " ? " + thenExpr + " : " + elseExpr + ";");
		return true;
	}

	static boolean handleLetMutTypedLiteral(String stmt,
																					Map<String, String> types,
																					Map<String, Boolean> mutable,
																					List<String> outputs) {
		Matcher m = Compiler.LET_MUT_TYPED_LITERAL_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		String cType = Compiler.toCType(m.group(3), m.group(4));
		types.put(m.group(1), cType);
		mutable.put(m.group(1), Boolean.TRUE);
		outputs.add(cType + " " + m.group(1) + " = " + m.group(2) + ";");
		return true;
	}

	static boolean handleLetMutDefaultI32(String stmt,
																				Map<String, String> types,
																				Map<String, Boolean> mutable,
																				List<String> outputs) {
		Matcher m = Compiler.LET_MUT_DEFAULT_I32_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		types.put(m.group(1), "int32_t");
		mutable.put(m.group(1), Boolean.TRUE);
		outputs.add("int32_t " + m.group(1) + " = " + m.group(2) + ";");
		return true;
	}

	static boolean handleAssignConditional(String stmt,
																				 Map<String, String> types,
																				 Map<String, Boolean> mutable,
																				 List<String> outputs) throws CompileException {
		Matcher m = Compiler.ASSIGN_CONDITIONAL_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		String varType = types.get(m.group(1));
		Boolean isMut = mutable.get(m.group(1));
		if (varType == null || isMut == null || !isMut) throw new CompileException();
		String cond = m.group(2), thenExpr = m.group(3), elseExpr = m.group(4);
		Compiler.validateCondition(cond, types);
		String t1 = Compiler.resolveExprType(thenExpr, types);
		String t2 = Compiler.resolveExprType(elseExpr, types);
		if (!t1.equals(t2) || !varType.equals(t1)) throw new CompileException();
		outputs.add(m.group(1) + " = " + cond + " ? " + thenExpr + " : " + elseExpr + ";");
		return true;
	}

	static boolean handleAssignTypedLiteral(String stmt,
																					Map<String, String> types,
																					Map<String, Boolean> mutable,
																					List<String> outputs) throws CompileException {
		Matcher m = Compiler.ASSIGN_TYPED_LITERAL_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		String varType = types.get(m.group(1));
		Boolean isMut = mutable.get(m.group(1));
		if (varType == null || isMut == null || !isMut) throw new CompileException();
		String litType = Compiler.toCType(m.group(3), m.group(4));
		if (!litType.equals(varType)) throw new CompileException();
		outputs.add(m.group(1) + " = " + m.group(2) + ";");
		return true;
	}

	static boolean handleAssignDefaultI32(String stmt,
																				Map<String, String> types,
																				Map<String, Boolean> mutable,
																				List<String> outputs) throws CompileException {
		Matcher m = Compiler.ASSIGN_DEFAULT_I32_PATTERN.matcher(stmt);
		if (!m.matches()) return false;
		String varType = types.get(m.group(1));
		Boolean isMut = mutable.get(m.group(1));
		if (varType == null || isMut == null || !isMut) throw new CompileException();
		if (!"int32_t".equals(varType)) throw new CompileException();
		outputs.add(m.group(1) + " = " + m.group(2) + ";");
		return true;
	}
}
