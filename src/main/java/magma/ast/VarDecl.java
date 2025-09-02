package magma.ast;

// Small holder for a parsed variable declaration
public record VarDecl(String name, String rhs, String type, boolean mut) implements SeqItem {


	@Override
	public String toString() {
		return "VarDecl[" + "name=" + name + ", " + "rhs=" + rhs + ", " + "type=" + type + ", " + "mut=" + mut + ']';
	}
}
