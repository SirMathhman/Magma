// Generated transpiled C++ from 'src\main\java\magma\compile\Serialize.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Serialize{};
template<typename T>
Result<T, CompileError> deserialize_Serialize(Class<T> clazz, Node node) {
	/*return deserializeValue(clazz, node).mapValue(clazz::cast);*/
}
template<typename T>
Result<Node, CompileError> serialize_Serialize(Class<T> clazz, T value) {
	/*return serializeValue(clazz, value);*/
}
Result<Node, CompileError> serializeValue_Serialize(Class</*?*/> type, Object value) {
	/*return serializeRecord(type, value);*/
}
Result<Node, CompileError> serializeSealed_Serialize(Class</*?*/> type, Object value) {
	/*final Class<?> concreteClass = value.getClass();*/
	/*return serializeValue(concreteClass, value);*/
}
Result<Node, CompileError> serializeRecord_Serialize(Class</*?*/> type, Object value) {
	/*Node result = createNodeWithType(type);*/
	/*List<CompileError> errors = new ArrayList<>();*/
	/*for (RecordComponent component : type.getRecordComponents()) {
			try {
				Object fieldValue = component.getAccessor().invoke(value);
				Result<Node, CompileError> fieldResult = serializeField(component, fieldValue);
				switch (fieldResult) {
					case Ok<Node, CompileError>(Node fieldNode) -> result = mergeNodes(result, fieldNode);
					case Err<Node, CompileError>(CompileError error) -> errors.add(error);
				}
			} catch (Exception e) {
				errors.add(new CompileError("Failed to read component '" + component.getName() + "'",
						new StringContext(type.getName()),
						List.of(new CompileError(e.getMessage(), new StringContext(component.getName())))));
			}
		}*/
	/*return errors.isEmpty() ? new Ok<>(result)
				: new Err<>(new CompileError("Failed to serialize '" + type.getSimpleName() + "'",
						new StringContext(type.getName()), errors));*/
}
Result<Node, CompileError> serializeField_Serialize(RecordComponent component, Object value) {
	/*String fieldName = component.getName();*/
	/*Class<?> fieldType = component.getType();*/
	/*return serializeValue(fieldType, value)
				.mapValue(childNode -> new Node().withNode(fieldName, childNode));*/
}
Result<Node, CompileError> serializeOptionField_Serialize(RecordComponent component, Object value) {
	/*String fieldName = component.getName();*/
	/*return new Ok<>(new Node());*/
}
Result<Node, CompileError> serializeOptionListField_Serialize(char* fieldName, Type listType, Object content) {
	/*Type elementType = getGenericArgument(listType, 0);*/
	/*Class<?> elementClass = erase(elementType);*/
	/*return serializeListElements(elementClass, list)
				.mapValue(nodes -> nodes.isEmpty() ? new Node() : new Node().withNodeList(fieldName, nodes));*/
}
Result<Node, CompileError> serializeListField_Serialize(RecordComponent component, Object value) {
	/*String fieldName = component.getName();*/
	/*Type elementType = getGenericArgument(component.getGenericType(), 0);*/
	/*Class<?> elementClass = erase(elementType);*/
	/*return serializeListElements(elementClass, list)
				.mapValue(nodes -> nodes.isEmpty() ? new Node() : new Node().withNodeList(fieldName, nodes));*/
}
Result<List<Node>, CompileError> serializeListElements_Serialize(Class</*?*/> elementClass, List</*?*/> list) {
	/*List<Node> nodes = new ArrayList<>();*/
	/*List<CompileError> errors = new ArrayList<>();*/
	/*for (Object element : list) {
			Result<Node, CompileError> elementResult = serializeValue(elementClass, element);
			if (elementResult instanceof Ok<Node, CompileError> ok) {
				nodes.add(ok.value());
			} else if (elementResult instanceof Err<Node, CompileError> err) {
				errors.add(err.error());
			}
		}*/
	/*return errors.isEmpty() ? new Ok<>(nodes)
				: new Err<>(new CompileError("Failed to serialize list elements", new StringContext("list"), errors));*/
}
Result<Object, CompileError> deserializeValue_Serialize(Class</*?*/> type, Node node) {
	/*return deserializeRecord(type, node);*/
}
Result<Object, CompileError> deserializeSealed_Serialize(Class</*?*/> type, Node node) {
	/*// Try direct permitted subclasses*/
	/*for (Class<?> permitted : type.getPermittedSubclasses()) {
			Option<String> maybeIdentifier = resolveTypeIdentifier(permitted);
			if (maybeIdentifier instanceof Some<String>(String identifier) && identifier.equals(nodeType)) {
				return deserializeValue(permitted, node);
			}
		}*/
	/*// Try nested sealed interfaces*/
	/*for (Class<?> permitted : type.getPermittedSubclasses()) {
			if (permitted.isSealed() && !permitted.isRecord()) {
				Result<Object, CompileError> recursiveResult = deserializeSealed(permitted, node);
				if (recursiveResult instanceof Ok<?, ?> ok && type.isAssignableFrom(ok.value().getClass())) {
					return recursiveResult;
				}
			}
		}*/
	/*return new Err<>(
				new CompileError("No permitted subtype of '" + type.getName() + "' matched node type '" + nodeType + "'",
						new NodeContext(node)));*/
}
Result<Object, CompileError> deserializeRecord_Serialize(Class</*?*/> type, Node node) {
	/*// Validate type annotation if present*/
	/*Option<String> expectedType = resolveTypeIdentifier(type);*/
	/*RecordComponent[] components = type.getRecordComponents();*/
	/*Object[] arguments = new Object[components.length];*/
	/*List<CompileError> errors = new ArrayList<>();*/
	/*Set<String> consumedFields = new HashSet<>();*/
	/*for (int i = 0;*/
	/*i < components.length;*/
	/*i++) {
			Result<Object, CompileError> componentResult = deserializeField(components[i], node, consumedFields);
			switch (componentResult) {
				case Ok<Object, CompileError>(Object value) -> arguments[i] = value;
				case Err<Object, CompileError>(CompileError error) -> errors.add(error);
			}
		}*/
	/*// Validate that all fields were consumed*/
	/*Option<CompileError> validationError = validateAllFieldsConsumed(node, consumedFields, type);*/
	/*try {
			Class<?>[] parameterTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new);
			Constructor<?> constructor = type.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return new Ok<>(constructor.newInstance(arguments));
		}*/
	/*catch (Exception e) {
			return new Err<>(new CompileError("Reflection failure while instantiating '" + type.getSimpleName() + "'",
					new NodeContext(node), List.of(new CompileError(e.getMessage(), new StringContext(type.getName())))));
		}*/
}
Result<Object, CompileError> deserializeField_Serialize(RecordComponent component, Node node, Set<String> consumedFields) {
	/*String fieldName = component.getName();*/
	/*Class<?> fieldType = component.getType();*/
	/*Option<Node> childNode = node.findNode(fieldName);*/
	/*else {
			return new Err<>(new CompileError("Required component '" + fieldName +
					"' of type '" + fieldType.getSimpleName() + "' not present", new NodeContext(node)));
		}*/
}
Result<Object, CompileError> deserializeStringField_Serialize(char* fieldName, Node node, Set<String> consumedFields) {
	/*Option<String> direct = node.findString(fieldName);*/
	/*// Try nested search*/
	/*Option<String> nested = findStringInChildren(node, fieldName);*/
	/*else {
			return new Err<>(new CompileError("Required component '" + fieldName +
					"' of type 'String' not present", new NodeContext(node)));
		}*/
}
Result<Object, CompileError> deserializeOptionField_Serialize(RecordComponent component, Node node, Set<String> consumedFields) {
	/*Type elementType = getGenericArgument(component.getGenericType(), 0);*/
	/*Class<?> elementClass = erase(elementType);*/
	/*String fieldName = component.getName();*/
	/*Option<Node> childNode = node.findNode(fieldName);*/
	/*else {
			return new Ok<>(Option.empty());
		}*/
}
Result<Object, CompileError> deserializeOptionListField_Serialize(char* fieldName, Type listType, Node node, Set<String> consumedFields) {
	/*Type elementType = getGenericArgument(listType, 0);*/
	/*Class<?> elementClass = erase(elementType);*/
	/*Option<List<Node>> maybeList = node.findNodeList(fieldName);*/
	/*else {
			return new Ok<>(Option.empty());
		}*/
}
Result<Object, CompileError> deserializeListField_Serialize(RecordComponent component, Node node, Set<String> consumedFields) {
	/*String fieldName = component.getName();*/
	/*Type elementType = getGenericArgument(component.getGenericType(), 0);*/
	/*Class<?> elementClass = erase(elementType);*/
	/*Option<List<Node>> maybeList = node.findNodeList(fieldName);*/
	/*else {
			return new Err<>(new CompileError("Required component '" + fieldName +
					"' of type 'List' not present", new NodeContext(node)));
		}*/
}
Result<List<Object>, CompileError> deserializeListElements_Serialize(Class</*?*/> elementClass, List<Node> nodeList) {
	/*List<Object> results = new ArrayList<>();*/
	/*List<CompileError> errors = new ArrayList<>();*/
	/*for (Node childNode : nodeList) {
			Result<Object, CompileError> childResult = deserializeValue(elementClass, childNode);
			if (childResult instanceof Ok<Object, CompileError> ok) {
				results.add(ok.value());
			} else if (childResult instanceof Err<Object, CompileError> err) {
				// Only treat as error if this looks like it should be deserializable
				if (shouldBeDeserializableAs(childNode, elementClass)) {
					errors.add(err.error());
				}
				// Otherwise silently skip (e.g., whitespace in lists)
			}
		}*/
	/*return errors.isEmpty() ? new Ok<>(results)
				: new Err<>(new CompileError("Failed to deserialize list elements", new NodeContext(nodeList.get(0)), errors));*/
}
Node createNodeWithType_Serialize(Class</*?*/> type) {
	/*Node node = new Node();*/
	/*Option<String> typeId = resolveTypeIdentifier(type);*/
	/*return node;*/
}
Node mergeNodes_Serialize(Node base, Node addition) {
	/*Node result = new Node();*/
	/*result.maybeType = base.maybeType;*/
	/*// Merge nodes and node lists by creating new node and copying fields*/
	/*// Note: We can't access private fields directly, so we create a new merged node*/
	/*// by using the public merge method*/
	/*result.merge(base);*/
	/*result.merge(addition);*/
	/*return result;*/
}
Type getGenericArgument_Serialize(Type type, int index) {
	/*throw new IllegalArgumentException("Type " + type + " does not have generic argument at index " + index);*/
}
Class</*?*/> erase_Serialize(Type type) {
	/*throw new IllegalArgumentException("Cannot erase type '" + type + "'");*/
}
Option<String> resolveTypeIdentifier_Serialize(Class</*?*/> clazz) {
	/*Tag annotation = clazz.getAnnotation(Tag.class);*/
	/*return Objects.isNull(annotation) ? Option.empty() : Option.of(annotation.value());*/
}
Option<String> findStringInChildren_Serialize(Node node, char* key) {
	/*for (Node child : node.nodes.values()) {
			Option<String> result = child.findString(key);
			if (result instanceof Some<String>) {
				return result;
			}
			result = findStringInChildren(child, key);
			if (result instanceof Some<String>) {
				return result;
			}
		}*/
	/*for (List<Node> children : node.nodeLists.values()) {
			for (Node child : children) {
				Option<String> result = child.findString(key);
				if (result instanceof Some<String>) {
					return result;
				}
				result = findStringInChildren(child, key);
				if (result instanceof Some<String>) {
					return result;
				}
			}
		}*/
	/*return Option.empty();*/
}
boolean shouldBeDeserializableAs_Serialize(Node node, Class</*?*/> targetClass) {
	/*return false;*/
}
Option<CompileError> validateAllFieldsConsumed_Serialize(Node node, Set<String> consumedFields, Class</*?*/> targetClass) {
	/*// Collect all field names from the Node*/
	/*Set<String> allFields = new HashSet<>();*/
	/*allFields.addAll(getStringKeys(node));*/
	/*allFields.addAll(node.nodes.keySet());*/
	/*allFields.addAll(node.nodeLists.keySet());*/
	/*// Find fields that were not consumed*/
	/*Set<String> leftoverFields = new HashSet<>(allFields);*/
	/*leftoverFields.removeAll(consumedFields);*/
	/*return Option.empty();*/
}
Set<String> getStringKeys_Serialize(Node node) {
	/*return node.getStringKeys();*/
}
