package magma.compile;

import magma.compile.context.NodeContext;
import magma.compile.context.TokenSequenceContext;
import magma.compile.error.CompileError;
import magma.compile.rule.RootTokenSequence;
import magma.compile.rule.TokenSequence;
import magma.list.ArrayList;
import magma.list.Joiner;
import magma.list.List;
import magma.list.NonEmptyList;
import magma.list.Stream;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Actual
public class JavaSerializer {
	// Public API
	public static <T> Result<T, CompileError> deserialize(Class<T> clazz, Node node) {
		if (Objects.isNull(clazz))
			return new Err<T, CompileError>(new CompileError("Target class must not be absent", new NodeContext(node)));
		if (Objects.isNull(node)) return new Err<T, CompileError>(new CompileError("Cannot deserialize absent node",
																																							 createContext(clazz.getName())));

		return deserializeValue(clazz, node).mapValue(clazz::cast);
	}

	public static <T> Result<Node, CompileError> serialize(Class<T> clazz, T value) {
		if (Objects.isNull(clazz)) return new Err<Node, CompileError>(new CompileError("Target class must not be absent",
																																									 createContext("serialize")));
		if (Objects.isNull(value)) return new Err<Node, CompileError>(new CompileError(
				"Cannot serialize absent instance of '" + clazz.getName() + "'",
				createContext("serialize")));

		return serializeValue(clazz, value);
	}

	// Pure recursive serialization
	private static Result<Node, CompileError> serializeValue(Class<?> type, Object value) {
		if (type.isSealed() && !type.isRecord()) return serializeSealed(type, value);
		if (!type.isRecord())
			return new Err<Node, CompileError>(new CompileError("Unsupported serialization target '" + type.getName() + "'",
																													createContext(type.getName())));
		return serializeRecord(type, value);
	}

	private static Result<Node, CompileError> serializeSealed(Class<?> type, Object value) {
		final Class<?> concreteClass = value.getClass();
		if (!type.isAssignableFrom(concreteClass)) return new Err<Node, CompileError>(new CompileError(
				"Instance of type '" + concreteClass.getName() + "' is not assignable to '" + type.getName() + "'",
				createContext(concreteClass.getName())));
		return serializeValue(concreteClass, value);
	}

	private static Result<Node, CompileError> serializeRecord(Class<?> type, Object value) {
		Node result = createNodeWithType(type);
		List<CompileError> errors = new ArrayList<CompileError>();

		RecordComponent[] recordComponents = type.getRecordComponents();
		int i = 0;
		while (i < recordComponents.length) {
			RecordComponent component = recordComponents[i];
			try {
				Object fieldValue = component.getAccessor().invoke(value);
				Result<Node, CompileError> fieldResult = serializeField(component, fieldValue);
				switch (fieldResult) {
					case Ok<Node, CompileError>(Node fieldNode) -> result = mergeNodes(result, fieldNode);
					case Err<Node, CompileError>(CompileError error) -> errors.addLast(error);
				}
			} catch (Exception e) {
				errors.addLast(new CompileError("Failed to read component '" + component.getName() + "'",
																				createContext(type.getName()),
																				List.of(new CompileError(e.getMessage(), createContext(component.getName())))));
			}
			i++;
		}

		if (errors.isEmpty()) return new Ok<Node, CompileError>(result);
		return new Err<Node, CompileError>(new CompileError("Failed to serialize '" + type.getSimpleName() + "'",
																												createContext(type.getName()),
																												errors));
	}

	private static TokenSequenceContext createContext(String type) {
		return new TokenSequenceContext(new RootTokenSequence(type));
	}

	private static Result<Node, CompileError> serializeField(RecordComponent component, Object value) {
		String fieldName = component.getName();
		Class<?> fieldType = component.getType();

		if (Objects.isNull(value))
			return new Err<Node, CompileError>(new CompileError("Component '" + fieldName + "' was absent",
																													createContext(fieldName)));

		if (fieldType == TokenSequence.class) return new Ok<Node, CompileError>(new Node().withSlice(fieldName, (TokenSequence) value));
		if (Option.class.isAssignableFrom(fieldType)) return serializeOptionField(component, value);
		if (NonEmptyList.class.isAssignableFrom(fieldType)) return serializeNonEmptyListField(component, value);
		if (List.class.isAssignableFrom(fieldType)) return serializeListField(component, value);
		return serializeValue(fieldType, value).mapValue(childNode -> new Node().withNode(fieldName, childNode));
	}

	private static Result<Node, CompileError> serializeOptionField(RecordComponent component, Object value) {
		String fieldName = component.getName();

		if (!(value instanceof Option<?> option))
			return new Err<Node, CompileError>(new CompileError("Component '" + fieldName + "' is not an Optional instance",
																													createContext(fieldName)));

		if (option instanceof None<?>) return new Ok<Node, CompileError>(new Node()); // Empty node for None

		if (option instanceof Some<?>(Object value1)) {
			Result<Type, CompileError> elementTypeResult = getGenericArgument(component.getGenericType());
			if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
				return new Err<Node, CompileError>(error);
			Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

			Result<Class<?>, CompileError> elementClassResult = erase(elementType);
			if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
				return new Err<Node, CompileError>(error);
			Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

			if (elementClass == TokenSequence.class)
				return new Ok<Node, CompileError>(new Node().withSlice(fieldName, (TokenSequence) value1));

			if (NonEmptyList.class.isAssignableFrom(elementClass))
				return serializeOptionNonEmptyListField(fieldName, elementType, value1);

			if (List.class.isAssignableFrom(elementClass)) return serializeOptionListField(fieldName, elementType, value1);

			return serializeValue(elementClass, value1).mapValue(childNode -> new Node().withNode(fieldName, childNode));
		}

		return new Ok<Node, CompileError>(new Node());
	}

	private static Result<Node, CompileError> serializeOptionListField(String fieldName, Type listType, Object content) {
		if (!(content instanceof List<?> list)) return new Err<Node, CompileError>(new CompileError(
				"Optional List component '" + fieldName + "' is not a List instance",
				createContext(fieldName)));

		Result<Type, CompileError> elementTypeResult = getGenericArgument(listType);
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Node, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Node, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

		return serializeListElements(elementClass, list).mapValue(nodes -> {
			if (nodes.isEmpty()) return new Node();
			// Convert to NonEmptyList since we know it's non-empty
			return NonEmptyList.fromList(nodes)
												 .map(nonEmptyNodes -> new Node().withNodeList(fieldName, nonEmptyNodes))
												 .orElse(new Node()); // Should never happen since we checked isEmpty
		});
	}

	private static Result<Node, CompileError> serializeOptionNonEmptyListField(String fieldName,
																																						 Type nonEmptyListType,
																																						 Object content) {
		if (!(content instanceof NonEmptyList<?> nonEmptyList)) return new Err<Node, CompileError>(new CompileError(
				"Optional NonEmptyList component '" + fieldName + "' is not a NonEmptyList instance",
				createContext(fieldName)));

		Result<Type, CompileError> elementTypeResult = getGenericArgument(nonEmptyListType);
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Node, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Node, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

		return serializeListElements(elementClass, nonEmptyList.toList()).mapValue(nodes -> NonEmptyList.fromList(nodes)
																																																		.map(nonEmptyNodes -> new Node().withNodeList(
																																																				fieldName,
																																																				nonEmptyNodes))
																																																		.orElse(new Node()));
	}

	private static Result<Node, CompileError> serializeNonEmptyListField(RecordComponent component, Object value) {
		String fieldName = component.getName();

		if (!(value instanceof NonEmptyList<?> nonEmptyList)) return new Err<Node, CompileError>(new CompileError(
				"Component '" + fieldName + "' is not a NonEmptyList instance",
				createContext(fieldName)));

		Result<Type, CompileError> elementTypeResult = getGenericArgument(component.getGenericType());
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Node, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Node, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

		// Convert NonEmptyList to List for serialization
		final List<?> list = nonEmptyList.toList();
		return serializeListElements(elementClass, list).mapValue(nodes -> {
			// NonEmptyList should always serialize to a non-empty list of nodes
			return NonEmptyList.fromList(nodes)
												 .map(nonEmptyNodes -> new Node().withNodeList(fieldName, nonEmptyNodes))
												 .orElse(new Node()); // Should never happen for a NonEmptyList
		});
	}

	private static Result<Node, CompileError> serializeListField(RecordComponent component, Object value) {
		String fieldName = component.getName();

		if (!(value instanceof List<?> list))
			return new Err<Node, CompileError>(new CompileError("Component '" + fieldName + "' is not a List instance",
																													createContext(fieldName)));

		Result<Type, CompileError> elementTypeResult = getGenericArgument(component.getGenericType());
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Node, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Node, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

		return serializeListElements(elementClass, list).mapValue(nodes -> {
			if (nodes.isEmpty()) return new Node();
			// NonEmptyList should never serialize to empty, but handle defensively
			return NonEmptyList.fromList(nodes)
												 .map(nonEmptyNodes -> new Node().withNodeList(fieldName, nonEmptyNodes))
												 .orElse(new Node()); // Should never happen for a NonEmptyList
		});
	}

	private static Result<List<Node>, CompileError> serializeListElements(Class<?> elementClass, List<?> list) {
		List<Node> nodes = new ArrayList<Node>();
		List<CompileError> errors = new ArrayList<CompileError>();

		list.stream().map(element -> serializeValue(elementClass, element)).forEach(elementResult -> {
			if (elementResult instanceof Ok<Node, CompileError>(Node value)) nodes.addLast(value);
			else if (elementResult instanceof Err<Node, CompileError>(CompileError error)) errors.addLast(error);
		});

		if (errors.isEmpty()) return new Ok<List<Node>, CompileError>(nodes);
		return new Err<List<Node>, CompileError>(new CompileError("Failed to serialize list elements",
																															createContext("list"),
																															errors));
	}

	// Pure recursive deserialization
	private static Result<Object, CompileError> deserializeValue(Class<?> type, Node node) {
		if (type.isSealed() && !type.isRecord()) return deserializeSealed(type, node);
		if (!type.isRecord()) return new Err<Object, CompileError>(new CompileError(
				"Unsupported deserialization target '" + type.getName() + "'", new NodeContext(node)));
		return deserializeRecord(type, node);
	}

	private static Result<Object, CompileError> deserializeSealed(Class<?> type, Node node) {
		if (!(node.maybeType instanceof Some<String>(String nodeType)))
			return new Err<Object, CompileError>(new CompileError(
					"Missing node type for sealed type '" + type.getName() + "'", new NodeContext(node)));

		// Try direct permitted subclasses
		Option<Result<Object, CompileError>> directResult = tryDirectPermittedSubclasses(type, node, nodeType);
		if (directResult instanceof Some<Result<Object, CompileError>>(Result<Object, CompileError> result)) return result;

		// Try nested sealed interfaces
		return tryNestedSealedInterfaces(type, node, nodeType);
	}

	private static Option<Result<Object, CompileError>> tryDirectPermittedSubclasses(Class<?> type,
																																									 Node node,
																																									 String nodeType) {
		Class<?>[] permittedSubclasses = type.getPermittedSubclasses();
		int i = 0;
		while (i < permittedSubclasses.length) {
			Class<?> permitted = permittedSubclasses[i];
			Option<String> maybeIdentifier = resolveTypeIdentifier(permitted);
			if (maybeIdentifier instanceof Some<String>(String identifier) && identifier.equals(nodeType))
				return new Some<Result<Object, CompileError>>(deserializeValue(permitted, node));
			i++;
		}
		return new None<Result<Object, CompileError>>();
	}

	private static Result<Object, CompileError> tryNestedSealedInterfaces(Class<?> type, Node node, String nodeType) {
		Option<Result<Object, CompileError>> recursiveResult = findNestedSealedDeserialization(type, node, nodeType);
		if (recursiveResult instanceof Some<Result<Object, CompileError>>(Result<Object, CompileError> value)) return value;

		// Collect all valid tags for better error message
		List<String> validTags = collectAllValidTags(type);

		String validTagsList;
		if (validTags.isEmpty()) validTagsList = "none";
		else validTagsList = validTags.stream().collect(new Joiner(", "));

		String suggestion = getSuggestionForUnknownTag(type, nodeType, validTags);
		return new Err<Object, CompileError>(new CompileError(
				"No permitted subtype of '" + type.getSimpleName() + "' matched node type '" + nodeType + "'. " +
				"Valid tags are: [" + validTagsList + "]. " + suggestion, new NodeContext(node)));
	}

	private static Option<Result<Object, CompileError>> findNestedSealedDeserialization(Class<?> type,
																																											Node node,
																																											String nodeType) {
		Class<?>[] subclasses = type.getPermittedSubclasses();
		int j = 0;
		while (j < subclasses.length) {
			Class<?> permitted = subclasses[j];
			Option<Result<Object, CompileError>> recursiveResult =
					tryDeserializeNestedSealed(type, node, nodeType, permitted);
			if (recursiveResult instanceof Some<Result<Object, CompileError>> k) return k;
			j++;
		}

		return new None<Result<Object, CompileError>>();
	}

	private static Option<Result<Object, CompileError>> tryDeserializeNestedSealed(Class<?> type,
																																								 Node node,
																																								 String nodeType,
																																								 Class<?> permitted) {
		if (!permitted.isSealed() || permitted.isRecord()) return new None<Result<Object, CompileError>>();

		Result<Object, CompileError> recursiveResult = deserializeSealed(permitted, node);
		if (recursiveResult instanceof Ok<?, ?>(Object value) && type.isAssignableFrom(value.getClass()))
			return new Some<Result<Object, CompileError>>(recursiveResult);
		// If recursiveResult is Err but would have matched (i.e., the tag was valid but
		// deserialization failed
		// for other reasons), propagate that error instead of generating a misleading
		// "unknown tag" error
		if (recursiveResult instanceof Err<?, ?> && canMatchType(permitted, nodeType))
			return new Some<Result<Object, CompileError>>(recursiveResult);
		return new None<Result<Object, CompileError>>();
	}

	private static String getSuggestionForUnknownTag(Class<?> type, String nodeType, List<String> validTags) {
		// Provide helpful suggestions for unknown tags
		if (validTags.isEmpty()) return "This sealed interface has no valid implementations with @Tag annotations.";

		// Find if there's a similar tag (simple Levenshtein-like check)
		Option<String> closestTag = findClosestTag(nodeType, validTags);
		if (closestTag instanceof Some<String>(String tag))
			return "Did you mean '" + tag + "'? Or add a record type with @Tag(\"" + nodeType +
						 "\") to the permitted subtypes of '" + type.getSimpleName() + "'.";

		return "Add a record type with @Tag(\"" + nodeType + "\") and include it in the 'permits' clause of '" +
					 type.getSimpleName() + "'.";
	}

	private static Option<String> findClosestTag(String nodeType, List<String> validTags) {
		Option<String> closest = Option.empty();
		int minDistance = Integer.MAX_VALUE;

		int i = 0;
		while (i < validTags.size()) {
			String tag = validTags.get(i).orElse(null);
			int distance = levenshteinDistance(nodeType.toLowerCase(), tag.toLowerCase());
			if (distance < minDistance && distance <= 2) // Only suggest if reasonably close
				minDistance = distance;
			closest = Option.of(tag);
			i++;
		}

		return closest;
	}

	private static int levenshteinDistance(String s1, String s2) {
		int[][] dp = new int[s1.length() + 1][s2.length() + 1];

		initializeFirstColumn(dp, s1);
		initializeFirstRow(dp, s2);
		fillLevenshteinMatrix(dp, s1, s2);

		return dp[s1.length()][s2.length()];
	}

	private static void initializeFirstColumn(int[][] dp, String s1) {
		int bound = s1.length();
		int i1 = 0;
		while (i1 <= bound) {
			dp[i1][0] = i1;
			i1++;
		}
	}

	private static void initializeFirstRow(int[][] dp, String s2) {
		int bound1 = s2.length();
		int j = 0;
		while (j <= bound1) {
			dp[0][j] = j;
			j++;
		}
	}

	private static void fillLevenshteinMatrix(int[][] dp, String s1, String s2) {
		int i = 1;
		while (i <= s1.length()) {
			fillLevenshteinRow(dp, s1, s2, i);
			i++;
		}
	}

	private static void fillLevenshteinRow(int[][] dp, String s1, String s2, int i) {
		int j = 1;
		while (j <= s2.length()) {
			if (s1.charAt(i - 1) == s2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
			else dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
			j++;
		}
	}

	private static List<String> collectAllValidTags(Class<?> sealedType) {
		List<String> tags = new ArrayList<String>();
		// Recursively collect from nested sealed interfaces
		Arrays.stream(sealedType.getPermittedSubclasses()).forEach(permitted -> {
			Option<String> maybeIdentifier = resolveTypeIdentifier(permitted);
			if (maybeIdentifier instanceof Some<String>(String tag)) tags.addLast(tag);
			if (permitted.isSealed() && !permitted.isRecord()) tags.addAll(collectAllValidTags(permitted));
		});
		return tags;
	}

	private static boolean canMatchType(Class<?> sealedType, String nodeType) {
		// Check if this sealed type or any of its permitted subtypes could match the
		// given node type
		Class<?>[] permittedSubclasses = sealedType.getPermittedSubclasses();
		int i = 0;
		while (i < permittedSubclasses.length) {
			Class<?> permitted = permittedSubclasses[i];
			Option<String> maybeIdentifier = resolveTypeIdentifier(permitted);
			if (maybeIdentifier instanceof Some<String>(String tag) && tag.equals(nodeType)) return true;
			// Recursively check nested sealed interfaces
			if (permitted.isSealed() && !permitted.isRecord()) if (canMatchType(permitted, nodeType)) return true;
			i++;
		}
		return false;
	}

	private static Result<Object, CompileError> deserializeRecord(Class<?> type, Node node) {
		// Validate type annotation if present
		Option<String> expectedType = resolveTypeIdentifier(type);
		if (expectedType instanceof Some<String>(String expectedType0))
			if (node.maybeType instanceof Some<String>(String nodeType)) {
				if (!node.is(expectedType0)) return new Err<Object, CompileError>(new CompileError(
						"Expected node type '" + expectedType0 + "' but found '" + nodeType + "'", new NodeContext(node)));
			} else return new Err<Object, CompileError>(new CompileError(
					"Node '@type' property missing for '" + type.getSimpleName() + "' (expected '@type': '" + expectedType0 +
					"')", new NodeContext(node)));

		RecordComponent[] components = type.getRecordComponents();
		Object[] arguments = new Object[components.length];
		List<CompileError> errors = new ArrayList<CompileError>();
		Set<String> consumedFields = new HashSet<String>();

		Stream.range(0, components.length).forEach(i -> {
			Result<Object, CompileError> componentResult = deserializeField(components[i], node, consumedFields);
			switch (componentResult) {
				case Ok<Object, CompileError>(Object value) -> arguments[i] = value;
				case Err<Object, CompileError>(CompileError error) -> errors.addLast(error);
			}
		});

		// Validate that all fields were consumed
		Option<CompileError> validationError = validateAllFieldsConsumed(node, consumedFields, type);
		if (validationError instanceof Some<CompileError>(CompileError error)) errors.addLast(error);

		if (!errors.isEmpty())
			return new Err<Object, CompileError>(new CompileError("Failed to deserialize '" + type.getSimpleName() + "'",
																														new NodeContext(node),
																														errors));

		try {
			Class<?>[] parameterTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new);
			Constructor<?> constructor = type.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return new Ok<Object, CompileError>(constructor.newInstance(arguments));
		} catch (Exception e) {
			return new Err<Object, CompileError>(new CompileError(
					"Reflection failure while instantiating '" + type.getSimpleName() + "'",
					new NodeContext(node),
					List.of(new CompileError(e.getMessage(), createContext(type.getName())))));
		}
	}

	private static Result<Object, CompileError> deserializeField(RecordComponent component,
																															 Node node,
																															 Set<String> consumedFields) {
		String fieldName = component.getName();
		Class<?> fieldType = component.getType();

		if (fieldType == TokenSequence.class) return deserializeSliceField(fieldName, node, consumedFields);
		if (Option.class.isAssignableFrom(fieldType)) return deserializeOptionField(component, node, consumedFields);
		if (NonEmptyList.class.isAssignableFrom(fieldType))
			return deserializeNonEmptyListField(component, node, consumedFields);

		if (List.class.isAssignableFrom(fieldType)) return deserializeListField(component, node, consumedFields);

		Option<Node> childNode = node.findNode(fieldName);
		if (childNode instanceof Some<Node>(Node value)) {
			consumedFields.add(fieldName);
			return deserializeValue(fieldType, value);
		} else return new Err<Object, CompileError>(new CompileError(
				"Required component '" + fieldName + "' of type '" + fieldType.getSimpleName() + "' not present",
				new NodeContext(node)));
	}

	private static Result<Object, CompileError> deserializeSliceField(String fieldName,
																																		Node node,
																																		Set<String> consumedFields) {
		Option<TokenSequence> direct = node.findSlice(fieldName);
		if (direct instanceof Some<TokenSequence>(TokenSequence value)) {
			consumedFields.add(fieldName);
			return new Ok<Object, CompileError>(value);
		}

		// Try nested search
		Option<TokenSequence> nested = findSliceInChildren(node, fieldName);
		if (nested instanceof Some<TokenSequence>(TokenSequence value)) {
			consumedFields.add(fieldName);
			return new Ok<Object, CompileError>(value);
		} else return new Err<Object, CompileError>(new CompileError(
				"Required component '" + fieldName + "' of type 'Slice' not present", new NodeContext(node)));
	}

	private static Result<Object, CompileError> deserializeOptionField(RecordComponent component,
																																		 Node node,
																																		 Set<String> consumedFields) {
		Result<Type, CompileError> elementTypeResult = getGenericArgument(component.getGenericType());
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();
		String fieldName = component.getName();

		if (elementClass == TokenSequence.class) {
			Option<TokenSequence> direct = node.findSlice(fieldName);
			if (direct instanceof Some<TokenSequence>(TokenSequence value)) {
				consumedFields.add(fieldName);
				return new Ok<Object, CompileError>(direct);
			}
			Option<TokenSequence> nested = findSliceInChildren(node, fieldName);
			if (nested instanceof Some<TokenSequence>(TokenSequence value)) {
				consumedFields.add(fieldName);
				return new Ok<Object, CompileError>(nested);
			}

			// Check if field exists but is wrong type (e.g., list when expecting slice)
			Option<Node> wrongTypeNode = node.findNode(fieldName);
			if (wrongTypeNode instanceof Some<Node>) return new Err<Object, CompileError>(new CompileError(
					"Field '" + fieldName + "' of type 'Option<Slice>' found a node instead of slice in '" +
					node.maybeType.orElse("unknown") + "'", new NodeContext(node)));
			Option<NonEmptyList<Node>> wrongTypeList = node.findNodeList(fieldName);
			if (wrongTypeList instanceof Some<NonEmptyList<Node>>) return new Err<Object, CompileError>(new CompileError(
					"Field '" + fieldName + "' of type 'Option<Slice>' found a list instead of slice in '" +
					node.maybeType.orElse("unknown") + "'",
					new NodeContext(node)));

			return new Ok<Object, CompileError>(Option.empty());
		}

		if (NonEmptyList.class.isAssignableFrom(elementClass))
			return deserializeOptionNonEmptyListField(fieldName, elementType, node, consumedFields);

		if (List.class.isAssignableFrom(elementClass))
			return deserializeOptionListField(fieldName, elementType, node, consumedFields);

		Option<Node> childNode = node.findNode(fieldName);
		if (childNode instanceof Some<Node>(Node value)) {
			consumedFields.add(fieldName);
			return deserializeValue(elementClass, value).mapValue(Option::of);
		} else return new Ok<Object, CompileError>(Option.empty());
	}

	private static Result<Object, CompileError> deserializeOptionListField(String fieldName,
																																				 Type listType,
																																				 Node node,
																																				 Set<String> consumedFields) {
		Result<Type, CompileError> elementTypeResult = getGenericArgument(listType);
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

		Option<NonEmptyList<Node>> maybeList = node.findNodeList(fieldName);
		if (maybeList instanceof Some<NonEmptyList<Node>>(NonEmptyList<Node> value)) {
			consumedFields.add(fieldName);
			Result<List<Object>, CompileError> elementsResult = deserializeListElements(elementClass, value.toList());
			return elementsResult.mapValue(list -> Option.of(list.copy()));
		} else return new Ok<Object, CompileError>(Option.empty());
	}

	private static Result<Object, CompileError> deserializeOptionNonEmptyListField(String fieldName,
																																								 Type nonEmptyListType,
																																								 Node node,
																																								 Set<String> consumedFields) {
		Result<Type, CompileError> elementTypeResult = getGenericArgument(nonEmptyListType);
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

		Option<NonEmptyList<Node>> maybeList = node.findNodeList(fieldName);
		if (maybeList instanceof Some<NonEmptyList<Node>>(NonEmptyList<Node> value)) {
			consumedFields.add(fieldName);
			Result<List<Object>, CompileError> elementsResult = deserializeListElements(elementClass, value.toList());
			if (elementsResult instanceof Err<List<Object>, CompileError>(CompileError error))
				return new Err<Object, CompileError>(error);

			List<Object> elements = ((Ok<List<Object>, CompileError>) elementsResult).value();
			Option<NonEmptyList<Object>> maybeNonEmpty = NonEmptyList.fromList(elements);
			if (maybeNonEmpty instanceof Some<NonEmptyList<Object>>(NonEmptyList<Object> nel))
				return new Ok<Object, CompileError>(Option.of(nel));
			return new Ok<Object, CompileError>(Option.empty());
		}

		return new Ok<Object, CompileError>(Option.empty());
	}

	private static Result<Object, CompileError> deserializeNonEmptyListField(RecordComponent component,
																																					 Node node,
																																					 Set<String> consumedFields) {
		String fieldName = component.getName();
		Result<Type, CompileError> elementTypeResult = getGenericArgument(component.getGenericType());
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

		Option<NonEmptyList<Node>> maybeList = node.findNodeList(fieldName);
		if (maybeList instanceof Some<NonEmptyList<Node>>(NonEmptyList<Node> value)) {
			consumedFields.add(fieldName);

			// NonEmptyList is never empty, so no need to check

			Result<List<Object>, CompileError> elementsResult = deserializeListElements(elementClass, value.toList());
			if (elementsResult instanceof Err<List<Object>, CompileError>(CompileError error))
				return new Err<Object, CompileError>(error);

			List<Object> elements = ((Ok<List<Object>, CompileError>) elementsResult).value();
			Option<NonEmptyList<Object>> nonEmptyOption = NonEmptyList.fromList(elements);

			if (nonEmptyOption instanceof Some<NonEmptyList<Object>>(
					NonEmptyList<Object> nel
			)) return new Ok<Object, CompileError>(nel);
			else return new Err<Object, CompileError>(new CompileError(
					"Failed to create NonEmptyList from deserialized elements for '" + fieldName + "'",
					new NodeContext(node)));
		} else return new Err<Object, CompileError>(new CompileError(
				"Required component '" + fieldName + "' of type 'NonEmptyList' not present", new NodeContext(node)));
	}

	private static Result<Object, CompileError> deserializeListField(RecordComponent component,
																																	 Node node,
																																	 Set<String> consumedFields) {
		String fieldName = component.getName();
		Result<Type, CompileError> elementTypeResult = getGenericArgument(component.getGenericType());
		if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

		Result<Class<?>, CompileError> elementClassResult = erase(elementType);
		if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
			return new Err<Object, CompileError>(error);
		Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();

		Option<NonEmptyList<Node>> maybeList = node.findNodeList(fieldName);
		if (maybeList instanceof Some<NonEmptyList<Node>>(NonEmptyList<Node> value)) {
			consumedFields.add(fieldName);
			Result<List<Object>, CompileError> elementsResult = deserializeListElements(elementClass, value.toList());
			return elementsResult.mapValue(List::copy);
		} else return new Err<Object, CompileError>(new CompileError(
				"Required component '" + fieldName + "' of type 'List' not present", new NodeContext(node)));
	}

	private static Result<List<Object>, CompileError> deserializeListElements(Class<?> elementClass,
																																						List<Node> nodeList) {
		List<Object> results = new ArrayList<Object>();
		List<CompileError> errors = new ArrayList<CompileError>();
		int index = 0;

		int i = 0;
		while (i < nodeList.size()) {
			Node childNode = nodeList.get(i).orElse(null);
			Result<Object, CompileError> childResult = deserializeValue(elementClass, childNode);
			if (childResult instanceof Ok<Object, CompileError>(Object value)) results.addLast(value);
			else // If the target is a sealed type and node has a type tag, check if it's an
				// unknown tag error
				// Otherwise silently skip (e.g., whitespace in lists without matching context)
				if (childResult instanceof Err<Object, CompileError>(CompileError error))
					if (elementClass.isSealed() && childNode.maybeType instanceof Some<String>(String nodeType)) {
						// For sealed types, a node with a type tag that doesn't match any permitted
						// type is always an error
						CompileError wrappedError = new CompileError(
								"Element at index " + index + " with type '" + nodeType + "' cannot be deserialized as '" +
								elementClass.getSimpleName() + "'", new NodeContext(childNode), List.of(error));
						errors.addLast(wrappedError);
					} else // For non-sealed types, only treat as error if it looks like it should match
						if (shouldBeDeserializableAs(childNode, elementClass)) errors.addLast(error);
			index++;
			i++;
		}

		if (errors.isEmpty()) return new Ok<List<Object>, CompileError>(results);
		return new Err<List<Object>, CompileError>(new CompileError(
				"Failed to deserialize " + errors.size() + " of " + nodeList.size() + " list elements as '" +
				elementClass.getSimpleName() + "'", new NodeContext(nodeList.getFirst().orElse(null)), errors));
	}

	// Pure helper functions
	private static Node createNodeWithType(Class<?> type) {
		Node node = new Node();
		Option<String> typeId = resolveTypeIdentifier(type);
		if (typeId instanceof Some<String>(String value)) node.retype(value);
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

	private static Result<Type, CompileError> getGenericArgument(Type type) {
		if (type instanceof ParameterizedType parameterized) {
			Type[] args = parameterized.getActualTypeArguments();
			if (args.length > 0) return new Ok<Type, CompileError>(args[0]);
		}

		return new Err<Type, CompileError>(new CompileError("Type " + type + " does not have generic argument at index 0",
																												createContext(type.toString())));
	}

	private static Result<Class<?>, CompileError> erase(Type type) {
		if (type instanceof Class<?> clazz) return new Ok<Class<?>, CompileError>(clazz);
		if (type instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> raw)
			return new Ok<Class<?>, CompileError>(raw);

		return new Err<Class<?>, CompileError>(new CompileError("Cannot erase type '" + type + "'",
																														createContext(type.toString())));
	}

	private static Option<String> resolveTypeIdentifier(Class<?> clazz) {
		Tag annotation = clazz.getAnnotation(Tag.class);
		if (Objects.isNull(annotation)) return Option.empty();
		return Option.of(annotation.value());
	}

	private static Option<TokenSequence> findSliceInChildren(Node node, String key) {
		{
			Iterator<Node> iterator = node.nodes.values().iterator();
			while (iterator.hasNext()) {
				Node child = iterator.next();
				Option<TokenSequence> result = child.findSlice(key);
				if (result instanceof Some<TokenSequence>) return result;
				result = findSliceInChildren(child, key);
				if (result instanceof Some<TokenSequence>) return result;
			}
		}
		return findSliceInNodeLists(node, key);
	}

	private static Option<TokenSequence> findSliceInNodeLists(Node node, String key) {
		Iterator<NonEmptyList<Node>> iterator = node.nodeLists.values().iterator();
		while (iterator.hasNext()) {
			List<Node> children = iterator.next().toList();
			Option<TokenSequence> result = searchChildrenList(children, key);
			if (result instanceof Some<TokenSequence>) return result;
		}
		return Option.empty();
	}

	private static Option<TokenSequence> searchChildrenList(List<Node> children, String key) {
		int i = 0;
		while (i < children.size()) {
			Node child = children.get(i).orElse(null);
			Option<TokenSequence> result = child.findSlice(key);
			if (result instanceof Some<TokenSequence>) return result;
			result = findSliceInChildren(child, key);
			if (result instanceof Some<TokenSequence>) return result;
			i++;
		}
		return Option.empty();
	}

	private static boolean shouldBeDeserializableAs(Node node, Class<?> targetClass) {
		if (node.maybeType instanceof None<String>) return false;

		if (node.maybeType instanceof Some<String>(String nodeType)) {
			Tag tagAnnotation = targetClass.getAnnotation(Tag.class);
			if (Objects.nonNull(tagAnnotation)) return nodeType.equals(tagAnnotation.value());

			String targetName = targetClass.getSimpleName().toLowerCase();
			return nodeType.toLowerCase().contains(targetName) || targetName.contains(nodeType.toLowerCase());
		}

		return false;
	}

	private static Option<CompileError> validateAllFieldsConsumed(Node node,
																																Set<String> consumedFields,
																																Class<?> targetClass) {
		// Collect all field names from the Node
		Set<String> allFields = new HashSet<String>();
		allFields.addAll(getStringKeys(node));
		allFields.addAll(node.nodes.keySet());
		allFields.addAll(node.nodeLists.keySet());

		// Find fields that were not consumed
		Set<String> leftoverFields = new HashSet<String>(allFields);
		leftoverFields.removeAll(consumedFields);

		if (!leftoverFields.isEmpty()) {
			String leftoverList = String.join(", ", leftoverFields);
			return Option.of(new CompileError(
					"Incomplete deserialization for '" + targetClass.getSimpleName() + "': leftover fields [" + leftoverList +
					"] were not consumed. " + "This indicates a mismatch between the Node structure and the target ADT.",
					new NodeContext(node)));
		}

		return Option.empty();
	}

	private static Set<String> getStringKeys(Node node) {
		return node.getStringKeys();
	}
}
