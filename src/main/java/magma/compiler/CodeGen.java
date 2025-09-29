package magma.compiler;

import magma.compiler.ast.*;

import java.io.FileWriter;

public class CodeGen {
	public static Result<Unit, java.io.IOException> generateC(magma.compiler.ast.Program program, String outPath) {
		try (FileWriter fw = new FileWriter(outPath)) {
			fw.write("#include <stdio.h>\n\n");
			fw.write("int main() {\n");
			for (Stmt s : program.getStatements()) {
				if (s instanceof PrintStmt) {
					PrintStmt ps = (PrintStmt) s;
					Expr e = ps.getExpr();
					if (e instanceof StringLiteral) {
						StringLiteral sl = (StringLiteral) e;
						fw.write("    printf(\"" + escapeForC(sl.getValue()) + "\");\n");
					}
				}
			}
			fw.write("    return 0;\n");
			fw.write("}\n");
			return Result.ok(Unit.instance());
		} catch (java.io.IOException ex) {
			return Result.err(ex);
		}
	}

	private static String escapeForC(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
	}
}
