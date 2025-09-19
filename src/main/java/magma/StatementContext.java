package magma;

public class StatementContext {
	public final java.util.Map<String, Long> env = new java.util.HashMap<>();
	public final java.util.Map<String, Boolean> mut = new java.util.HashMap<>();
	public final java.util.Map<String, String> types = new java.util.HashMap<>();
	public final java.util.Map<String, String> refs = new java.util.HashMap<>();
	public final java.util.Map<String, Boolean> refsMutable = new java.util.HashMap<>();
}
