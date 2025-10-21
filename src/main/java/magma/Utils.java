package magma;

import magma.Functions.Function;

public class Utils {
	public record Tuple<A, B>(A left, B right) {
		public static <A, B, R> Function<Tuple<A, B>, Tuple<R, B>> mapLeft(Function<A, R> mapper) {
			return arg -> new Tuple<R, B>(mapper.apply(arg.left), arg.right);
		}
	}
}
