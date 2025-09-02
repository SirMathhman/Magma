package magma.ast;

import java.util.Objects;

// Small holder for a parsed variable declaration
public final class VarDecl implements magma.ast.SeqItem {
	public final String name;
	public final String rhs;
	public final String type;
	public final boolean mut;

	public VarDecl(String name, String rhs, String type, boolean mut) {
		this.name = name;
		this.rhs = rhs;
		this.type = type;
		this.mut = mut;
	}

	public String name() {return name;}

	public String rhs() {return rhs;}

	public String type() {return type;}

	public boolean mut() {return mut;}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (VarDecl) obj;
		return Objects.equals(this.name, that.name) && Objects.equals(this.rhs, that.rhs) &&
					 Objects.equals(this.type, that.type) && this.mut == that.mut;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, rhs, type, mut);
	}

	@Override
	public String toString() {
		return "VarDecl[" + "name=" + name + ", " + "rhs=" + rhs + ", " + "type=" + type + ", " + "mut=" + mut + ']';
	}
}
