/*import magma.MapNode;*//*import java.lang.reflect.InvocationTargetException;*//*import java.util.ArrayList;*//*import java.util.List;*//*import java.util.Optional;*//*public record TypeRule<Node>(Class<Node> clazz, Rule<MapNode> rule) implements Rule<Node> {
	@Override
	public Optional<Node> lex(final String input) {
		return this.rule.lex(input).flatMap(this::deserialize);
	}

	private Optional<Node> deserialize(final MapNode node) {
		try {
			final var arguments = this.collectArguments(node);
			final var argumentTypes = arguments.stream().map(Object::getClass).toArray(Class[]::new);

			return Optional.of(this.clazz.getConstructor(argumentTypes).newInstance(arguments.toArray()));
		} catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
									 NoSuchMethodException e) {
			return Optional.empty();
		}
	}

	private List<Object> collectArguments(final MapNode node) {
		final var arguments = new ArrayList<Object>();
		for (final var field : this.clazz.getDeclaredFields()) {
			final var name = field.getName();
			//noinspection ObjectAllocationInLoop
			node.findString(name).ifPresent(arguments::add);
		}
		return arguments;
	}

	@Override
	public Optional<String> generate(final Node node) {
		try {
			return this.serialize(node);
		} catch (final IllegalAccessException e) {
			return Optional.empty();
		}
	}

	private Optional<String> serialize(final Node node) throws IllegalAccessException {
		var mapNode = new MapNode();
		for (final var field : this.clazz.getDeclaredFields()) {
			field.setAccessible(true);
			final var value = field.get(node);
			mapNode = mapNode.withString(field.getName(), (String) value);
		}

		return this.rule.generate(mapNode);
	}
}*/