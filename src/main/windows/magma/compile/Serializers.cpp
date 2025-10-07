// Generated transpiled C++ from 'src\main\java\magma\compile\Serializers.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Serializers {};
Result<T, CompileError> deserialize_Serializers() {
	return JavaSerializer.deserialize();
}
Result<Node, CompileError> serialize_Serializers() {
	return JavaSerializer.serialize();
}
