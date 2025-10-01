struct Serialize {};/*
	public static <T> Result<T, CompileError> deserialize(Class<T> clazz, Node node) {
		if (Objects.isNull(clazz))
			return new Err<>(new CompileError("Target class must not be absent", new NodeContext(node)));
		if (Objects.isNull(node))
			return new Err<>(new CompileError("Cannot deserialize absent node", new StringContext(clazz.getName())));

		if (clazz.isSealed() && !clazz.isRecord()) return deserializeSealed(clazz, node);
		if (!clazz.isRecord()) return new Err<>(
				new CompileError("Unsupported deserialization target '" + clazz.getName() + "'", new NodeContext(node)));

		final Option<String> expectedType = resolveTypeIdentifier(clazz);
		if (expectedType instanceof Some<String>(String expectedType0))
			if (node.maybeType instanceof Some<String>(String type)) {
				if (!node.is(expectedType0)) return new Err<>(
						new CompileError("Expected node type '" + expectedType0 + "' but found '" + type + "'",
														 new NodeContext(node)));
			} else return new Err<>(new CompileError(
					"Node '@type' property missing for '" + clazz.getSimpleName() + "' (expected '@type': '" + expectedType0 +
					"')", new NodeContext(node)));

		final RecordComponent[] components = clazz.getRecordComponents();
		final Object[] arguments = new Object[components.length];
		final ArrayList<CompileError> errors = new ArrayList<>();

		for (int i = 0; i < components.length; i++) {
			final RecordComponent component = components[i];
			final Result<Object, CompileError> componentResult = deserializeComponent(component, node);
			if (componentResult instanceof Ok<Object, CompileError>(Object value)) arguments[i] = value;
			else if (componentResult instanceof Err<Object, CompileError>(CompileError error)) errors.add(error);
		}

		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Failed to deserialize '" + clazz.getSimpleName() + "'", new NodeContext(node), errors));

		try {
			final Class<?>[] parameterTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new);
			final Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
			// Ensure constructor is accessible for reflection
			constructor.setAccessible(true);
			return new Ok<>(constructor.newInstance(arguments));
		} catch (ReflectiveOperationException e) {
			return new Err<>(new CompileError("Reflection failure while instantiating '" + clazz.getSimpleName() + "'",
																				new NodeContext(node),
																				List.of(new CompileError(e.getMessage(), new StringContext(clazz.getName())))));
		}
	}*//*

	public static <T> Result<Node, CompileError> serialize(Class<T> clazz, T node) {
		if (Objects.isNull(clazz))
			return new Err<>(new CompileError("Target class must not be absent", new StringContext("serialize")));
		if (Objects.isNull(node)) return new Err<>(
				new CompileError("Cannot serialize absent instance of '" + clazz.getName() + "'",
												 new StringContext("serialize")));
		if (clazz.isSealed() && !clazz.isRecord()) {
			@SuppressWarnings("unchecked")
			final Class<? extends T> concreteClass = (Class<? extends T>) node.getClass();
			if (!clazz.isAssignableFrom(concreteClass)) return new Err<>(new CompileError(
					"Instance of type '" + concreteClass.getName() + "' is not assignable to '" + clazz.getName() + "'",
					new StringContext(concreteClass.getName())));
			return serializeRaw(concreteClass, concreteClass.cast(node));
		}

		if (!clazz.isRecord()) return new Err<>(
				new CompileError("Unsupported serialization target '" + clazz.getName() + "'",
												 new StringContext(clazz.getName())));

		final Node result = new Node(); Option<String> stringOption = resolveTypeIdentifier(clazz);
		if (stringOption instanceof Some<String>(String value2)) result.retype(value2);

		final RecordComponent[] components = clazz.getRecordComponents();
		final ArrayList<CompileError> errors = new ArrayList<>();

		for (RecordComponent component : components) {
			final Method accessor = component.getAccessor();
			try {
				final Object value = accessor.invoke(node);
				final Option<CompileError> writeResult = writeComponent(result, component, value);
				if (writeResult instanceof Some<CompileError>(CompileError value1)) errors.add(value1);
			} catch (IllegalAccessException | InvocationTargetException e) {
				errors.add(new CompileError("Failed to read component '" + component.getName() + "'",
																		new StringContext(clazz.getName()),
																		List.of(new CompileError(e.getMessage(), new StringContext(component.getName())))));
			}
		}
		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Failed to serialize '" + clazz.getSimpleName() + "'", new StringContext(clazz.getName()),
												 errors));

		return new Ok<>(result);
	}*//*

	private static Option<String> resolveTypeIdentifier(Class<?> clazz) {
		Tag annotation = clazz.getAnnotation(Tag.class); if (Objects.isNull(annotation)) return Option.empty();
		return Option.of(annotation.value());
	}*//*

	private static <T> Result<T, CompileError> deserializeSealed(Class<T> clazz, Node node) {
		if (!(node.maybeType instanceof Some<String>(String nodeType))) return new Err<>(
				new CompileError("Missing node type for sealed type '" + clazz.getName() + "'", new NodeContext(node)));

		for (Class<?> permitted : clazz.getPermittedSubclasses()) {
			final Option<String> maybeIdentifier = resolveTypeIdentifier(permitted);
			if (maybeIdentifier instanceof Some<String>(String identifier) && identifier.equals(nodeType)) {
				@SuppressWarnings({"rawtypes", "unchecked"})
				final Result<T, CompileError> cast = (Result<T, CompileError>) deserialize((Class) permitted, node);
				return cast;
			}
		}

		return new Err<>(
				new CompileError("No permitted subtype of '" + clazz.getName() + "' matched node type '" + nodeType + "'",
												 new NodeContext(node)));
	}*//*

	private static Result<Object, CompileError> deserializeComponent(RecordComponent component, Node node) {
		final String key = component.getName();
		final Class<?> type = component.getType();

		if (type == String.class) {
			final Option<String> direct = node.findString(key);
			if (direct instanceof Some<String>(String directValue)) return new Ok<>(directValue);
			final Option<String> nested = findStringInChildren(node, key);
			if (nested instanceof Some<String>(String nestedValue)) return new Ok<>(nestedValue);
			return new Err<>(missingFieldError(key, type, node));
		}

		if (Option.class.isAssignableFrom(type)) return deserializeOptionalComponent(component, node);

		if (List.class.isAssignableFrom(type)) return deserializeListComponent(component, node);

		return deserializeNestedComponent(component, node);
	}*//*

	private static Result<Object, CompileError> deserializeOptionalComponent(RecordComponent component, Node node) {
		final Type genericType = component.getGenericType();
		if (!(genericType instanceof ParameterizedType parameterized) || parameterized.getActualTypeArguments().length != 1)
			return new Err<>(
					new CompileError("Optional component '" + component.getName() + "' must declare a single generic parameter",
													 new NodeContext(node)));

		final Type argumentType = parameterized.getActualTypeArguments()[0];
		final Class<?> elementClass = erase(argumentType);
		final String key = component.getName();

		if (elementClass == String.class) {
			final Option<String> direct = node.findString(key);
			if (direct instanceof Some<String>) return new Ok<>(direct);
			final Option<String> nested = findStringInChildren(node, key);
			if (nested instanceof Some<String>) return new Ok<>(nested);
			// Return empty Optional if not found
			return new Ok<>(Option.empty());
		}

		// For non-String Optional types, look for nested objects
		final Option<Node> maybeChild = node.findNode(key);
		if (maybeChild instanceof Some<Node>(Node child)) {
			final Result<Object, CompileError> childResult = deserializeRaw(elementClass, child);
			return childResult.map(Option::of);
		}

		return new Ok<>(Option.empty());
	}*//*

	private static Result<Object, CompileError> deserializeListComponent(RecordComponent component, Node node) {
		final Type genericType = component.getGenericType();
		if (!(genericType instanceof ParameterizedType parameterized) || parameterized.getActualTypeArguments().length != 1)
			return new Err<>(
					new CompileError("Component '" + component.getName() + "' must declare a single generic parameter",
													 new NodeContext(node)));

		final Type argumentType = parameterized.getActualTypeArguments()[0];
		final Class<?> elementClass = erase(argumentType);
		final Option<List<Node>> maybeList = node.findNodeList(component.getName());
		if (!(maybeList instanceof Some<List<Node>>(List<Node> list)))
			return new Err<>(missingFieldError(component.getName(), elementClass, node));

		final ArrayList<Object> results = new ArrayList<>();
		final ArrayList<CompileError> errors = new ArrayList<>();
		for (Node child : list) {
			final Result<Object, CompileError> childResult = deserializeRaw(elementClass, child);
			switch (childResult) {
				case Err<Object, CompileError> v -> errors.add(v.error());
				case Ok<Object, CompileError> v -> results.add(v.value());
			}
		}

		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Failed to deserialize list component '" + component.getName() + "'", new NodeContext(node),
												 errors));

		return new Ok<>(List.copyOf(results));
	}*//*

	private static Result<Object, CompileError> deserializeNestedComponent(RecordComponent component, Node node) {
		final String key = component.getName(); final Option<Node> maybeChild = node.findNode(key);
		if (maybeChild instanceof Some<Node>(Node child)) return deserializeRaw(component.getType(), child);
		return new Err<>(missingFieldError(key, component.getType(), node));
	}*//*

	private static Result<Object, CompileError> deserializeRaw(Class<?> type, Node node) {
		@SuppressWarnings({"rawtypes", "unchecked"})
		final Result<Object, CompileError> rawResult = (Result<Object, CompileError>) deserialize((Class) type, node);
		return rawResult;
	}*//*

	private static Result<Node, CompileError> serializeRaw(Class<?> clazz, Object value) {
		@SuppressWarnings({"rawtypes", "unchecked"})
		final Result<Node, CompileError> result = (Result<Node, CompileError>) serialize((Class) clazz, value);
		return result;
	}*//*

	private static CompileError missingFieldError(String key, Class<?> type, Node node) {
		return new CompileError("Required component '" + key + "' of type '" + type.getSimpleName() + "' not present",
														new NodeContext(node));
	}*//*

	private static Option<String> findStringInChildren(Node node, String key) {
		for (Node child : node.nodes.values()) {
			final Option<String> nested = switch (child.findString(key)) {
				case None<String> _ -> findStringInChildren(child, key);
				case Some<String> _ -> child.findString(key);
			};

			if (nested instanceof Some<String>) return nested;
		}
		for (List<Node> children : node.nodeLists.values())
			for (Node child : children) {
				final Option<String> nested = switch (child.findString(key)) {
					case None<String> _ -> findStringInChildren(child, key);
					case Some<String> _ -> child.findString(key);
				};

				if (nested instanceof Some<String>) return nested;
			} return Option.empty();
	}*//*

	private static Option<CompileError> writeComponent(Node target, RecordComponent component, Object value) {
		final String key = component.getName();
		final Class<?> type = component.getType();

		final CompileError absentError = new CompileError("Component '" + key + "' was absent", new StringContext(key));
		if (type == String.class) {
			if (Objects.isNull(value)) return Option.of(absentError);
			target.withString(key, (String) value); return Option.empty();
		}

		if (Option.class.isAssignableFrom(type)) return writeOptionalComponent(target, component, value);

		if (List.class.isAssignableFrom(type)) return writeListComponent(target, component, value);

		if (Objects.isNull(value)) return Option.of(absentError);

		final Result<Node, CompileError> nestedResult = serializeRaw(type, value);

		return switch (nestedResult) {
			case Err<Node, CompileError> v -> Option.of(v.error());
			case Ok<Node, CompileError> v -> {
				target.withNode(key, v.value()); yield Option.empty();
			}
		};
	}*//*

	private static Option<CompileError> writeOptionalComponent(Node target, RecordComponent component, Object value) {
		if (Objects.isNull(value)) return Option.of(
				new CompileError("Optional component '" + component.getName() + "' was absent",
												 new StringContext(component.getName())));
		if (!(value instanceof Option<?> maybeOptionValue)) return Option.of(
				new CompileError("Component '" + component.getName() + "' is not an Optional instance",
												 new StringContext(component.getName())));

		// If Optional is empty, don't write anything to the node (absence indicates
		// empty)
		if (!(maybeOptionValue instanceof Some<?>(Object content))) return Option.empty();

		final Type genericType = component.getGenericType();
		if (!(genericType instanceof ParameterizedType parameterized) || parameterized.getActualTypeArguments().length != 1)
			return Option.of(
					new CompileError("Optional component '" + component.getName() + "' must declare a single generic parameter",
													 new StringContext(component.getName())));

		final Class<?> elementClass = erase(parameterized.getActualTypeArguments()[0]);
		final String key = component.getName();

		if (elementClass == String.class) {
			target.withString(key, (String) content); return Option.empty();
		}

		// For non-String Optional types, serialize as nested objects
		final Result<Node, CompileError> serialized = serializeRaw(elementClass, content);
		return switch (serialized) {
			case Err<Node, CompileError> v -> Option.of(v.error());
			case Ok<Node, CompileError> v -> {
				target.withNode(key, v.value()); yield Option.empty();
			}
		};
	}*//*

	private static Option<CompileError> writeListComponent(Node target, RecordComponent component, Object value) {
		if (Objects.isNull(value)) return Option.of(
				new CompileError("Component '" + component.getName() + "' was absent", new StringContext(component.getName())));
		if (!(value instanceof List<?> listValue)) return Option.of(
				new CompileError("Component '" + component.getName() + "' is not a List instance",
												 new StringContext(component.getName())));

		final Type genericType = component.getGenericType();
		if (!(genericType instanceof ParameterizedType parameterized) || parameterized.getActualTypeArguments().length != 1)
			return Option.of(
					new CompileError("Component '" + component.getName() + "' must declare a single generic parameter",
													 new StringContext(component.getName())));

		final Class<?> elementClass = erase(parameterized.getActualTypeArguments()[0]);
		final ArrayList<Node> serializedChildren = new ArrayList<>();
		final ArrayList<CompileError> errors = new ArrayList<>();

		for (Object element : listValue) {
			final Result<Node, CompileError> serialized = serializeRaw(elementClass, element);
			switch (serialized) {
				case Err<Node, CompileError> v -> errors.add(v.error());
				case Ok<Node, CompileError> v -> serializedChildren.add(v.value());
			}
		}

		if (!errors.isEmpty()) return Option.of(
				new CompileError("Failed to serialize list component '" + component.getName() + "'",
												 new StringContext(component.getName()), errors));

		target.withNodeList(component.getName(), List.copyOf(serializedChildren)); return Option.empty();
	}*//*

	private static Class<?> erase(Type type) {
		if (type instanceof Class<?> clazz) return clazz;
		if (type instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> raw)
			return raw;
		throw new IllegalArgumentException("Cannot erase type '" + type + "'");
	}*//*
*/