package magma.compile;

import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Serialize {
	// Public API
	public static <T> Result<T, CompileError> deserialize(Class<T> clazz, Node node) {
		if (Objects.isNull(clazz))
			return new Err<>(new CompileError("Target class must not be absent", new NodeContext(node)));
		if (Objects.isNull(node))
			return new Err<>(new CompileError("Cannot deserialize absent node", new StringContext(clazz.getName())));

		return deserializeValue(clazz, node).mapValue(clazz::cast);
	}

	public static <T> Result<Node, CompileError> serialize(Class<T> clazz, T value) {
		if (Objects.isNull(clazz))
			return new Err<>(new CompileError("Target class must not be absent", new StringContext("serialize")));
		if (Objects.isNull(value))
			return new Err<>(new CompileError("Cannot serialize absent instance of '" + clazz.getName() + "'",
					new StringContext("serialize")));

		return serializeValue(clazz, value);
	}

	// Pure recursive serialization
	private static Result<Node, CompileError> serializeValue(Class<?> type, Object value) {
		if (type.isSealed() && !type.isRecord()) {
			return serializeSealed(type, value);
		}
		if (!type.isRecord()) {
			return new Err<>(new CompileError("Unsupported serialization target '" + type.getName() + "'",
					new StringContext(type.getName())));
		}
		return serializeRecord(type, value);
	}

	private static Result<Node, CompileError> serializeSealed(Class<?> type, Object value) {
		final Class<?> concreteClass = value.getClass();
		if (!type.isAssignableFrom(concreteClass)) {
			return new Err<>(new CompileError(
					"Instance of type '" + concreteClass.getName() + "' is not assignable to '" + type.getName() + "'",
					new StringContext(concreteClass.getName())));
		}
		return serializeValue(concreteClass, value);
	}

	private static Result<Node, CompileError> serializeRecord(Class<?> type, Object value) {
		Node result = createNodeWithType(type);
		List<CompileError> errors = new ArrayList<>();

		for (RecordComponent component : type.getRecordComponents()) {
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
		}

		return errors.isEmpty() ? new Ok<>(result)
				: new Err<>(new CompileError("Failed to serialize '" + type.getSimpleName() + "'",
						new StringContext(type.getName()), errors));
	}

	private static Result<Node, CompileError> serializeField(RecordComponent component, Object value) {
		String fieldName = component.getName();
		Class<?> fieldType = component.getType();

		if (Objects.isNull(value)) {
			return new Err<>(new CompileError("Component '" + fieldName + "' was absent",
					new StringContext(fieldName)));
		}

		if (fieldType == String.class) {
			return new Ok<>(new Node().withString(fieldName, (String) value));
		}

		if (Option.class.isAssignableFrom(fieldType)) {
			return serializeOptionField(component, value);
		}

		if (List.class.isAssignableFrom(fieldType)) {
			return serializeListField(component, value);
		}

		return serializeValue(fieldType, value)
				.mapValue(childNode -> new Node().withNode(fieldName, childNode));
	}

	private static Result<Node, CompileError> serializeOptionField(RecordComponent component, Object value) {
		String fieldName = component.getName();

		if (!(value instanceof Option<?> option)) {
			return new Err<>(new CompileError("Component '" + fieldName + "' is not an Optional instance",
					new StringContext(fieldName)));
		}

		if (option instanceof None<?>) {
			return new Ok<>(new Node()); // Empty node for None
		}

		if (option instanceof Some<?> some) {
			Object content = some.value();
			Type elementType = getGenericArgument(component.getGenericType(), 0);
			Class<?> elementClass = erase(elementType);

			if (elementClass == String.class) {
				return new Ok<>(new Node().withString(fieldName, (String) content));
			}

			if (List.class.isAssignableFrom(elementClass)) {
				return serializeOptionListField(fieldName, elementType, content);
			}

			return serializeValue(elementClass, content)
					.mapValue(childNode -> new Node().withNode(fieldName, childNode));
		}

		return new Ok<>(new Node());
	}

	private static Result<Node, CompileError> serializeOptionListField(String fieldName, Type listType, Object content) {
		if (!(content instanceof List<?> list)) {
			return new Err<>(new CompileError("Optional List component '" + fieldName + "' is not a List instance",
					new StringContext(fieldName)));
		}

		Type elementType = getGenericArgument(listType, 0);
		Class<?> elementClass = erase(elementType);

		return serializeListElements(elementClass, list)
				.mapValue(nodes -> nodes.isEmpty() ? new Node() : new Node().withNodeList(fieldName, nodes));
	}

	private static Result<Node, CompileError> serializeListField(RecordComponent component, Object value) {
		String fieldName = component.getName();

		if (!(value instanceof List<?> list)) {
			return new Err<>(new CompileError("Component '" + fieldName + "' is not a List instance",
					new StringContext(fieldName)));
		}

		Type elementType = getGenericArgument(component.getGenericType(), 0);
		Class<?> elementClass = erase(elementType);

		return serializeListElements(elementClass, list)
				.mapValue(nodes -> nodes.isEmpty() ? new Node() : new Node().withNodeList(fieldName, nodes));
	}

	private static Result<List<Node>, CompileError> serializeListElements(Class<?> elementClass, List<?> list) {
		List<Node> nodes = new ArrayList<>();
		List<CompileError> errors = new ArrayList<>();

		for (Object element : list) {
			Result<Node, CompileError> elementResult = serializeValue(elementClass, element);
			if (elementResult instanceof Ok<Node, CompileError> ok) {
				nodes.add(ok.value());
			} else if (elementResult instanceof Err<Node, CompileError> err) {
				errors.add(err.error());
			}
		}

		return errors.isEmpty() ? new Ok<>(nodes)
				: new Err<>(new CompileError("Failed to serialize list elements", new StringContext("list"), errors));
	}

	// Pure recursive deserialization
	private static Result<Object, CompileError> deserializeValue(Class<?> type, Node node) {
		if (type.isSealed() && !type.isRecord()) {
			return deserializeSealed(type, node);
		}
		if (!type.isRecord()) {
			return new Err<>(new CompileError("Unsupported deserialization target '" + type.getName() + "'",
					new NodeContext(node)));
		}
		return deserializeRecord(type, node);
	}

	private static Result<Object, CompileError> deserializeSealed(Class<?> type, Node node) {
		if (!(node.maybeType instanceof Some<String>(String nodeType))) {
			return new Err<>(new CompileError("Missing node type for sealed type '" + type.getName() + "'",
					new NodeContext(node)));
		}

		// Try direct permitted subclasses
		for (Class<?> permitted : type.getPermittedSubclasses()) {
			Option<String> maybeIdentifier = resolveTypeIdentifier(permitted);
			if (maybeIdentifier instanceof Some<String>(String identifier) && identifier.equals(nodeType)) {
				return deserializeValue(permitted, node);
			}
		}

		// Try nested sealed interfaces
		for (Class<?> permitted : type.getPermittedSubclasses()) {
			if (permitted.isSealed() && !permitted.isRecord()) {
				Result<Object, CompileError> recursiveResult = deserializeSealed(permitted, node);
				if (recursiveResult instanceof Ok<?, ?> ok && type.isAssignableFrom(ok.value().getClass())) {
					return recursiveResult;
				}
			}
		}

		return new Err<>(
				new CompileError("No permitted subtype of '" + type.getName() + "' matched node type '" + nodeType + "'",
						new NodeContext(node)));
	}

	private static Result<Object, CompileError> deserializeRecord(Class<?> type, Node node) {
		// Validate type annotation if present
		Option<String> expectedType = resolveTypeIdentifier(type);
		if (expectedType instanceof Some<String>(String expectedType0)) {
			if (node.maybeType instanceof Some<String>(String nodeType)) {
				if (!node.is(expectedType0)) {
					return new Err<>(new CompileError("Expected node type '" + expectedType0 + "' but found '" + nodeType + "'",
							new NodeContext(node)));
				}
			} else {
				return new Err<>(new CompileError("Node '@type' property missing for '" + type.getSimpleName() +
						"' (expected '@type': '" + expectedType0 + "')", new NodeContext(node)));
			}
		}

		RecordComponent[] components = type.getRecordComponents();
		Object[] arguments = new Object[components.length];
		List<CompileError> errors = new ArrayList<>();
		Set<String> consumedFields = new HashSet<>();

		for (int i = 0; i < components.length; i++) {
			Result<Object, CompileError> componentResult = deserializeField(components[i], node, consumedFields);
			switch (componentResult) {
				case Ok<Object, CompileError>(Object value) -> arguments[i] = value;
				case Err<Object, CompileError>(CompileError error) -> errors.add(error);
			}
		}

		// Validate that all fields were consumed
		Option<CompileError> validationError = validateAllFieldsConsumed(node, consumedFields, type);
		if (validationError instanceof Some<CompileError>(CompileError error)) {
			errors.add(error);
		}

		if (!errors.isEmpty()) {
			return new Err<>(new CompileError("Failed to deserialize '" + type.getSimpleName() + "'",
					new NodeContext(node), errors));
		}

		try {
			Class<?>[] parameterTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new);
			Constructor<?> constructor = type.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return new Ok<>(constructor.newInstance(arguments));
		} catch (Exception e) {
			return new Err<>(new CompileError("Reflection failure while instantiating '" + type.getSimpleName() + "'",
					new NodeContext(node), List.of(new CompileError(e.getMessage(), new StringContext(type.getName())))));
		}
	}

	private static Result<Object, CompileError> deserializeField(RecordComponent component, Node node,
			Set<String> consumedFields) {
		String fieldName = component.getName();
		Class<?> fieldType = component.getType();

		if (fieldType == String.class) {
			return deserializeStringField(fieldName, node, consumedFields);
		}

		if (Option.class.isAssignableFrom(fieldType)) {
			return deserializeOptionField(component, node, consumedFields);
		}

		if (List.class.isAssignableFrom(fieldType)) {
			return deserializeListField(component, node, consumedFields);
		}

		Option<Node> childNode = node.findNode(fieldName);
		if (childNode instanceof Some<Node> some) {
			consumedFields.add(fieldName);
			return deserializeValue(fieldType, some.value());
		} else {
			return new Err<>(new CompileError("Required component '" + fieldName +
					"' of type '" + fieldType.getSimpleName() + "' not present", new NodeContext(node)));
		}
	}

	private static Result<Object, CompileError> deserializeStringField(String fieldName, Node node,
			Set<String> consumedFields) {
		Option<String> direct = node.findString(fieldName);
		if (direct instanceof Some<String>(String value)) {
			consumedFields.add(fieldName);
			return new Ok<>(value);
		}

		// Try nested search
		Option<String> nested = findStringInChildren(node, fieldName);
		if (nested instanceof Some<String>(String value)) {
			consumedFields.add(fieldName);
			return new Ok<>(value);
		} else {
			return new Err<>(new CompileError("Required component '" + fieldName +
					"' of type 'String' not present", new NodeContext(node)));
		}
	}

	private static Result<Object, CompileError> deserializeOptionField(RecordComponent component, Node node,
			Set<String> consumedFields) {
		Type elementType = getGenericArgument(component.getGenericType(), 0);
		Class<?> elementClass = erase(elementType);
		String fieldName = component.getName();

		if (elementClass == String.class) {
			Option<String> direct = node.findString(fieldName);
			if (direct instanceof Some<String>) {
				consumedFields.add(fieldName);
				return new Ok<>(direct);
			}
			Option<String> nested = findStringInChildren(node, fieldName);
			if (nested instanceof Some<String>) {
				consumedFields.add(fieldName);
				return new Ok<>(nested);
			}

			// Check if field exists but is wrong type (e.g., list when expecting string)
			Option<Node> wrongTypeNode = node.findNode(fieldName); if (wrongTypeNode instanceof Some<Node>) {
				return new Err<>(new CompileError(
						"Field '" + fieldName + "' of type 'Option<String>' found a node instead of string in '" +
						node.maybeType.orElse("unknown") + "'", new NodeContext(node)));
			} Option<List<Node>> wrongTypeList = node.findNodeList(fieldName);
			if (wrongTypeList instanceof Some<List<Node>>) {
				return new Err<>(new CompileError(
						"Field '" + fieldName + "' of type 'Option<String>' found a list instead of string in '" +
						node.maybeType.orElse("unknown") + "'", new NodeContext(node)));
			}
			
			return new Ok<>(Option.empty());
		}

		if (List.class.isAssignableFrom(elementClass)) {
			return deserializeOptionListField(fieldName, elementType, node, consumedFields);
		}

		Option<Node> childNode = node.findNode(fieldName);
		if (childNode instanceof Some<Node> some) {
			consumedFields.add(fieldName);
			return deserializeValue(elementClass, some.value()).mapValue(Option::of);
		} else {
			return new Ok<>(Option.empty());
		}
	}

	private static Result<Object, CompileError> deserializeOptionListField(String fieldName, Type listType, Node node,
			Set<String> consumedFields) {
		Type elementType = getGenericArgument(listType, 0);
		Class<?> elementClass = erase(elementType);

		Option<List<Node>> maybeList = node.findNodeList(fieldName);
		if (maybeList instanceof Some<List<Node>> some) {
			consumedFields.add(fieldName);
			Result<List<Object>, CompileError> elementsResult = deserializeListElements(elementClass, some.value());
			return elementsResult.mapValue(list -> Option.of(List.copyOf(list)));
		} else {
			return new Ok<>(Option.empty());
		}
	}

	private static Result<Object, CompileError> deserializeListField(RecordComponent component, Node node,
			Set<String> consumedFields) {
		String fieldName = component.getName();
		Type elementType = getGenericArgument(component.getGenericType(), 0);
		Class<?> elementClass = erase(elementType);

		Option<List<Node>> maybeList = node.findNodeList(fieldName);
		if (maybeList instanceof Some<List<Node>> some) {
			consumedFields.add(fieldName);
			Result<List<Object>, CompileError> elementsResult = deserializeListElements(elementClass, some.value());
			return elementsResult.mapValue(list -> List.copyOf(list));
		} else {
			return new Err<>(new CompileError("Required component '" + fieldName +
					"' of type 'List' not present", new NodeContext(node)));
		}
	}

	private static Result<List<Object>, CompileError> deserializeListElements(Class<?> elementClass,
			List<Node> nodeList) {
		List<Object> results = new ArrayList<>();
		List<CompileError> errors = new ArrayList<>();

		for (Node childNode : nodeList) {
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
		}

		return errors.isEmpty() ? new Ok<>(results)
				: new Err<>(new CompileError("Failed to deserialize list elements", new NodeContext(nodeList.get(0)), errors));
	}

	// Pure helper functions
	private static Node createNodeWithType(Class<?> type) {
		Node node = new Node();
		Option<String> typeId = resolveTypeIdentifier(type);
		if (typeId instanceof Some<String>(String value)) {
			node.retype(value);
		}
		return node;
	}

	private static Node mergeNodes(Node base, Node addition) {
		Node result = new Node();
		result.maybeType = base.maybeType;

		// Merge nodes and node lists by creating new node and copying fields
		// Note: We can't access private fields directly, so we create a new merged node
		// by using the public merge method
		result.merge(base);
		result.merge(addition);

		return result;
	}

	private static Type getGenericArgument(Type type, int index) {
		if (type instanceof ParameterizedType parameterized) {
			Type[] args = parameterized.getActualTypeArguments();
			if (args.length > index) {
				return args[index];
			}
		}
		throw new IllegalArgumentException("Type " + type + " does not have generic argument at index " + index);
	}

	private static Class<?> erase(Type type) {
		if (type instanceof Class<?> clazz) {
			return clazz;
		}
		if (type instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> raw) {
			return raw;
		}
		throw new IllegalArgumentException("Cannot erase type '" + type + "'");
	}

	private static Option<String> resolveTypeIdentifier(Class<?> clazz) {
		Tag annotation = clazz.getAnnotation(Tag.class);
		return Objects.isNull(annotation) ? Option.empty() : Option.of(annotation.value());
	}

	private static Option<String> findStringInChildren(Node node, String key) {
		for (Node child : node.nodes.values()) {
			Option<String> result = child.findString(key);
			if (result instanceof Some<String>) {
				return result;
			}
			result = findStringInChildren(child, key);
			if (result instanceof Some<String>) {
				return result;
			}
		}
		for (List<Node> children : node.nodeLists.values()) {
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
		}
		return Option.empty();
	}

	private static boolean shouldBeDeserializableAs(Node node, Class<?> targetClass) {
		if (node.maybeType instanceof None<String>) {
			return false;
		}

		if (node.maybeType instanceof Some<String>(String nodeType)) {
			Tag tagAnnotation = targetClass.getAnnotation(Tag.class);
			if (Objects.nonNull(tagAnnotation)) {
				return nodeType.equals(tagAnnotation.value());
			}

			String targetName = targetClass.getSimpleName().toLowerCase();
			return nodeType.toLowerCase().contains(targetName) || targetName.contains(nodeType.toLowerCase());
		}

		return false;
	}

	private static Option<CompileError> validateAllFieldsConsumed(Node node, Set<String> consumedFields,
			Class<?> targetClass) {
		// Collect all field names from the Node
		Set<String> allFields = new HashSet<>();
		allFields.addAll(getStringKeys(node));
		allFields.addAll(node.nodes.keySet());
		allFields.addAll(node.nodeLists.keySet());

		// Find fields that were not consumed
		Set<String> leftoverFields = new HashSet<>(allFields);
		leftoverFields.removeAll(consumedFields);

		if (!leftoverFields.isEmpty()) {
			String leftoverList = String.join(", ", leftoverFields);
			return Option.of(new CompileError(
					"Incomplete deserialization for '" + targetClass.getSimpleName() +
							"': leftover fields [" + leftoverList + "] were not consumed. " +
							"This indicates a mismatch between the Node structure and the target ADT.",
					new NodeContext(node)));
		}

		return Option.empty();
	}

	private static Set<String> getStringKeys(Node node) {
		return node.getStringKeys();
	}
}
