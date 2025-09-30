package magma.compile.rule;

public interface Folder {
	DivideState fold(DivideState state, char c);
}
