package magma;

/**
 * A simple generic immutable pair.
 *
 * @param <L> left element type
 * @param <R> right element type
 */
public record Tuple<L, R>(L left, R right) {
	/**
	 * Static factory method for convenience.
	 */
	public static <L, R> Tuple<L, R> of(L left, R right) {
		return new Tuple<>(left, right);
	}
}
