/*

public */struct Main {};
/*private interface Rule {
		Optional<String> generate(MapNode node);

		Optional<MapNode> lex(String input);
	}*//*

	public record StringRule(String key) implements Rule {
		@Override
		public Optional<MapNode> lex(String content) {
			return Optional.of(new MapNode().withString(key, content));
		}

		@Override
		public Optional<String> generate(MapNode node) {
			return node.findString(key);
		}
	}*//*

	public record PlaceholderRule(Rule childRule) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return childRule.generate(node).map(Main::wrap);
		}

		@Override
		public Optional<MapNode> lex(String input) {
			return childRule.lex(input);
		}
	}*//*

	public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
		@Override
		public Optional<MapNode> lex(String input) {
			final var index = input.indexOf(infix());
			if (index < 0) return Optional.empty();

			final var leftSlice = input.substring(0, index);
			final var rightSlice = input.substring(index + infix().length());
			return leftRule.lex(leftSlice).flatMap(withModifiers -> rightRule.lex(rightSlice).map(withModifiers::merge));
		}

		@Override
		public Optional<String> generate(MapNode node) {
			return leftRule.generate(node)
										 .flatMap(
												 leftResult -> rightRule.generate(node).map(rightResult -> leftResult + infix + rightResult));
		}
	}*//*

	private record StripRule(Rule rule) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return rule.generate(node);
		}

		@Override
		public Optional<MapNode> lex(String input) {
			return rule.lex(input.strip());
		}
	}*//*

	private record SuffixRule(Rule childRule, String suffix) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return childRule.generate(node).map(result -> result + suffix);
		}

		@Override
		public Optional<MapNode> lex(String input) {
			if (!input.endsWith(suffix)) return Optional.empty();
			final var content = input.substring(0, input.length() - suffix.length());
			return childRule.lex(content);
		}
	}*//*

	private record TypeRule(String type, Rule rule) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			if (node.is(type)) return rule.generate(node);
			return Optional.empty();
		}

		@Override
		public Optional<MapNode> lex(String input) {
			return rule.lex(input).map(node -> node.retype(type));
		}
	}*//*

	private record OrRule(List<Rule> rules) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return rules.stream().map(rule -> rule.generate(node)).flatMap(Optional::stream).findFirst();
		}

		@Override
		public Optional<MapNode> lex(String input) {
			return rules.stream().map(rule -> rule.lex(input)).flatMap(Optional::stream).findFirst();
		}
	}*//*

	private record PrefixRule(String prefix, Rule rule) implements Rule {
		@Override
		public Optional<String> generate(MapNode node) {
			return rule.generate(node).map(result -> prefix + result);
		}

		@Override
		public Optional<MapNode> lex(String input) {
			if (!input.startsWith(prefix)) return Optional.empty();
			final var content = input.substring(prefix.length());
			return rule.lex(content);
		}
	}*//*

	private record DivideRule(String key, Rule rule) implements Rule {
		private static Stream<String> divide(CharSequence input) {
			var current = new State();
			for (var i = 0; i < input.length(); i++) {
				final var c = input.charAt(i);
				current = fold(current, c);
			}

			return current.advance().stream();
		}

		private static State fold(State current, char c) {
			final var appended = current.append(c);
			if (c == ';' && appended.isLevel()) return appended.advance();
			if (c == '}' && appended.isShallow()) return appended.advance().exit();
			if (c == '{') return appended.enter();
			if (c == '}') return appended.exit();
			return appended;
		}*//*

		@Override
		public Optional<MapNode> lex(String input) {
			final var list = divide(input).map(rule::lex).flatMap(Optional::stream).toList();
			return Optional.of(new MapNode().withNodeList(key, list));
		}*//*

		@Override
		public Optional<String> generate(MapNode root) {
			return Optional.of(String.join("", root.findNodeList(key)
																						 .orElse(Collections.emptyList())
																						 .stream()
																						 .map(rule::generate)
																						 .flatMap(Optional::stream)
																						 .toList()));
		}*//*
	*//*

	private static */struct LazyRule implements Rule {};
/*private Optional<Rule> maybeRule = Optional.empty();*//*

		public void set(Rule rule) {
			maybeRule = Optional.of(rule);
		}*//*

		@Override
		public Optional<String> generate(MapNode node) {
			return maybeRule.flatMap(rule -> rule.generate(node));
		}*//*

		@Override
		public Optional<MapNode> lex(String input) {
			return maybeRule.flatMap(rule -> rule.lex(input));
		}*//*
	*/int main(){
	return 0;
}