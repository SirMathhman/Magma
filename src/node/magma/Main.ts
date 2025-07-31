export class Main  {/*private Main() {}

	private static String compileRoot(final String input) {
		final var lex = Main.lex(input);
		final var list = Main.modify(lex);
		return Main.generate(list);
	}

	private static List<Node> modify(final Collection<Node> items) {
		return items.stream().filter(node -> !node.is("package") || !node.is("import")).toList();
	}

	private static String generate(final Collection<Node> nodes) {
		return nodes.stream()
								.map(Main.createTSRootSegmentRule()::generate)
								.flatMap(Optional::stream)
								.collect(Collectors.joining());
	}

	private static List<Node> lex(final String input) {
		return Main.divide(input)
							 .stream()
							 .map(input1 -> Main.createJavaRootSegmentRule().lex(input1))
							 .flatMap(Optional::stream)
							 .toList();
	}

	private static Collection<String> divide(final String input) {
		DivideState current = new MutableDivideState(input);
		while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final var next = maybeNext.get();
			current = Main.fold(next.left(), next.right());
		}

		return current.advance().stream().toList();
	}

	private static DivideState fold(final DivideState state, final char c) {
		final var appended = state.append(c);
		if ('{' == c) return appended.enter();
		else if ('}' == c) return appended.exit();
		else if (';' == c && appended.isLevel())
			return appended.advance();
		return appended;
	}

	private static OrRule createTSRootSegmentRule() {
		return new OrRule(List.of(Main.createTSClassRule(), Main.createTypePlaceholderRule()));
	}

	private static OrRule createJavaRootSegmentRule() {
		return new OrRule(
				List.of(Main.createNamespacedRule("package"), Main.createNamespacedRule("import"), Main.createJavaClassRule(),
								Main.createTypePlaceholderRule()));
	}

	private static TypeRule createTypePlaceholderRule() {
		return new TypeRule("placeholder", new PlaceholderRule(new StringRule("content")));
	}

	private static Rule createNamespacedRule(final String type) {
		final var content = new PrefixRule(type + " ", new SuffixRule(new StringRule("content"), ";"));
		return new TypeRule(type, new StripRule(content));
	}

	private static InfixRule createTSClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new StringRule("body");

		return new InfixRule(new SuffixRule(new PrefixRule("export class ", name), " {"), "",
												 new SuffixRule(new PlaceholderRule(body), "}"));
	}

	private static InfixRule createJavaClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new StringRule("body");

		// More flexible rule that can handle modifiers before "class"
		// and additional content between class name and opening brace
		return new InfixRule(new StringRule("modifiers"), "class ",
												 new InfixRule(name, "{", new StripRule(new SuffixRule(body, "}"))));
	}

	public static void main(final String[] args) {
		try {
			final String content = Files.readString(Paths.get("src/java/magma/Main.java"));
			final Path targetPath = Path.of("./src/node/magma/Main.ts");
			Files.createDirectories(targetPath.getParent());
			Files.writeString(targetPath, Main.compileRoot(content));
		} catch (final IOException e) {
			System.err.println("Error copying file: " + e.getMessage());
		}
	}
*/}