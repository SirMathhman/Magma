package magma.emit;

import magma.ast.VarDecl;

public final class EmitterCommon {
	private EmitterCommon() {
	}

	public static boolean isFunctionTyped(VarDecl d) {
		if (d == null) return false;
		var t = d.type();
		return t != null && t.contains("=>");
	}
}
