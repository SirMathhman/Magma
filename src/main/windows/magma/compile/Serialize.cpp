// Generated transpiled C++ from 'src\main\java\magma\compile\Serialize.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Serialize{};
template<typename T>
Result<T, CompileError> deserialize_Serialize(Class<T> clazz, Node node) {}
template<typename T>
Result<Node, CompileError> serialize_Serialize(Class<T> clazz, T value) {}
Result<Node, CompileError> serializeValue_Serialize(Class</*?*/> type, Object value) {}
Result<Node, CompileError> serializeSealed_Serialize(Class</*?*/> type, Object value) {}
Result<Node, CompileError> serializeRecord_Serialize(Class</*?*/> type, Object value) {}
Result<Node, CompileError> serializeField_Serialize(RecordComponent component, Object value) {}
Result<Node, CompileError> serializeOptionField_Serialize(RecordComponent component, Object value) {}
Result<Node, CompileError> serializeOptionListField_Serialize(char* fieldName, Type listType, Object content) {}
Result<Node, CompileError> serializeListField_Serialize(RecordComponent component, Object value) {}
Result<List<Node>, CompileError> serializeListElements_Serialize(Class</*?*/> elementClass, List</*?*/> list) {}
Result<Object, CompileError> deserializeValue_Serialize(Class</*?*/> type, Node node) {}
Result<Object, CompileError> deserializeSealed_Serialize(Class</*?*/> type, Node node) {}
Result<Object, CompileError> deserializeRecord_Serialize(Class</*?*/> type, Node node) {}
Result<Object, CompileError> deserializeField_Serialize(RecordComponent component, Node node, Set<String> consumedFields) {}
Result<Object, CompileError> deserializeStringField_Serialize(char* fieldName, Node node, Set<String> consumedFields) {}
Result<Object, CompileError> deserializeOptionField_Serialize(RecordComponent component, Node node, Set<String> consumedFields) {}
Result<Object, CompileError> deserializeOptionListField_Serialize(char* fieldName, Type listType, Node node, Set<String> consumedFields) {}
Result<Object, CompileError> deserializeListField_Serialize(RecordComponent component, Node node, Set<String> consumedFields) {}
Result<List<Object>, CompileError> deserializeListElements_Serialize(Class</*?*/> elementClass, List<Node> nodeList) {}
Node createNodeWithType_Serialize(Class</*?*/> type) {}
Node mergeNodes_Serialize(Node base, Node addition) {}
Type getGenericArgument_Serialize(Type type, int index) {}
Class</*?*/> erase_Serialize(Type type) {}
Option<String> resolveTypeIdentifier_Serialize(Class</*?*/> clazz) {}
Option<String> findStringInChildren_Serialize(Node node, char* key) {}
boolean shouldBeDeserializableAs_Serialize(Node node, Class</*?*/> targetClass) {}
Option<CompileError> validateAllFieldsConsumed_Serialize(Node node, Set<String> consumedFields, Class</*?*/> targetClass) {}
Set<String> getStringKeys_Serialize(Node node) {}
