package magma;

public class Functions {
	public interface Function<T, R> {
		R apply(T arg);
	}

	public interface BiFunction<A, B, R> {
		R apply(A first, B second);
	}
}
