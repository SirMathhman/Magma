// Generated transpiled C++ from 'src\main\java\magma\compile\Serializers.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Serializers {};
Result<> deserialize_Serializers(Class<> clazz, Node node) {
	return JavaSerializer.deserialize(clazz, node);
}
Result<> serialize_Serializers(Class<> clazz, T root) {
	return JavaSerializer.serialize(clazz, root);
}
