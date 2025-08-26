package magma.result;

public sealed interface Result<V, E> permits Ok, Err {
	boolean isErr();
}
