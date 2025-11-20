package magma.ast;

public class ImportStatement implements Statement {
	private final String module;
	private final boolean isExtern;

	public ImportStatement(String module, boolean isExtern) {
		this.module = module;
		this.isExtern = isExtern;
	}

	public String getModule() {
		return module;
	}

	public boolean isExtern() {
		return isExtern;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitImportStatement(this);
	}
}

