// Generated transpiled C++ from 'src\main\java\magma\compile\Serializers.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Serializers {};
Result<T, CompileError> deserialize_Serializers(Class<T> clazz, Node node) {
	return JavaSerializer.deserialize(clazz, node);
}
Result<Node, CompileError> serialize_Serializers(Class<T> clazz, T root) {
	return JavaSerializer.serialize(clazz, root);
}
