package magma.compile;

public class JNodes {
	@Tag("cast")
	public record JCast(Lang.JType type, Lang.JExpression child) implements Lang.JExpression {}
}
