package magma.compile;

import magma.compile.error.CompileError;
import magma.result.Result;

public class Serializers {
	@Actual
	public static <T> Result<T, CompileError> deserialize(Class<T> clazz, Node node) {
		return JavaSerializer.deserialize(clazz, node);
	}

	@Actual
	public static <T> Result<Node, CompileError> serialize(Class<T> clazz, T root) {
		return JavaSerializer.serialize(clazz, root);
	}
}
