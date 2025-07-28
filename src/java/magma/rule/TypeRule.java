package magma.rule;

import magma.MapNode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;

public record TypeRule<Node>(Class<Node> clazz, Rule<MapNode> rule) implements Rule<Node> {
	@Override
	public Optional<Node> lex(final String input) {
		return this.rule.lex(input).flatMap(this::deserialize);
	}

	private Optional<Node> deserialize(final MapNode node) {
		try {
			final var arguments = this.collectArguments(node);
			return Optional.of(this.clazz.getConstructor().newInstance(arguments.toArray()));
		} catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
									 NoSuchMethodException e) {
			return Optional.empty();
		}
	}

	private ArrayList<String> collectArguments(final MapNode node) {
		final var arguments = new ArrayList<String>();
		for (final var field : this.clazz.getFields()) {
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
		for (final var field : this.clazz.getFields()) {
			final var value = field.get(node);
			mapNode = mapNode.withString(field.getName(), (String) value);
		}

		return this.rule.generate(mapNode);
	}
}
