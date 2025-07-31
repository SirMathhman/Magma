export class Main {/*{
	private Main() {}

	private static String wrapInComment(final String content) {
		return "/*" + System.lineSeparator() + content + System.lineSeparator() + "*/";
	}

	private static String compileRoot(final String input) {
		return String.join("", Main.divide(input).stream().map(Main::compileRootSegment).toList());
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

	private static Optional<String> extractClassName(final String declaration) {
		final int classIndex = declaration.indexOf("class ");
		if (-1 == classIndex) return Optional.empty();
		final int start = classIndex + 6;
		final int end = declaration.indexOf(' ', start);
		return Optional.of(-1 == end ? declaration.substring(start) : declaration.substring(start, end));
	}

	private static Optional<String> extractClassBody(final String declaration) {
		final int openBrace = declaration.indexOf('{');
		if (-1 == openBrace) return Optional.empty();
		return Optional.of(declaration.substring(openBrace));
	}

	private static Optional<String> compileClass(final String input) {
		return Main.extractClassBody(input)
							 .flatMap(body -> Main.extractClassName(input)
																		.map(name -> Main.generate(
																				new MapNode().withString("name", name).withString("body", body))));
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		if (strip.contains("class ")) return Main.compileClass(strip).orElseGet(() -> Main.wrapInComment(strip));
		return Main.wrapInComment(strip);
	}

	private static String generate(final Node node) {
		return Main.createClassRule().generate(node).orElse("");
	}

	private static InfixRule createClassRule() {
		final Rule nameRule = new StringRule("name");
		final Rule bodyRule = new StringRule("body");

		return new InfixRule(new SuffixRule(new PrefixRule(nameRule, "export class "), " {"),
												 new SuffixRule(new PlaceholderRule(bodyRule), "}"), "");
	}
	
	private static InfixRule createSimpleClassRule() {
		final Rule nameRule = new StringRule("name");
		final Rule bodyRule = new StringRule("body");

		// Create a very simple rule for testing
		// Format: "name:body"
		return new InfixRule(nameRule, bodyRule, ":");
	}

	public static void main(final String[] args) {
		try {
			final String content = Files.readString(Paths.get("src/java/magma/Main.java"));
			final Path targetPath = Path.of("./src/node/magma/Main.ts");
			Files.createDirectories(targetPath.getParent());
			Files.writeString(targetPath, Main.compileRoot(content));
			
			// Test the lex method
			testLexMethod();
		} catch (final IOException e) {
			System.err.println("Error copying file: " + e.getMessage());
		}
	}
	
	private static void testLexMethod() {
		System.out.println("\n=== Testing individual rules ===");
		
		// Test StringRule
		testStringRule();
		
		// Test PrefixRule
		testPrefixRule();
		
		// Test SuffixRule
		testSuffixRule();
		
		// Test PlaceholderRule
		testPlaceholderRule();
		
		// Test InfixRule
		testInfixRule();
		
		System.out.println("\n=== Testing complex rule (class rule) ===");
		// Create a test node
		Node testNode = new MapNode();
		testNode = testNode.withString("name", "TestClass");
		testNode = testNode.withString("body", "{ test body }");
		
		// Generate a string from the node using the complex class rule
		Rule classRule = createClassRule();
		String generated = classRule.generate(testNode).orElse("");
		System.out.println("Generated with complex rule: " + generated);
		
		// Lex the string back to a node
		Optional<Node> lexedNode = classRule.lex(generated);
		if (lexedNode.isPresent()) {
			Node node = lexedNode.get();
			System.out.println("Lexed name: " + node.findString("name").orElse(""));
			System.out.println("Lexed body: " + node.findString("body").orElse(""));
		} else {
			System.out.println("Failed to lex with complex rule");
		}
		
		// Try with a simpler class rule for testing
		System.out.println("\n--- Testing with simpler class rule ---");
		Rule simpleClassRule = createSimpleClassRule();
		String simpleGenerated = simpleClassRule.generate(testNode).orElse("");
		System.out.println("Generated with simple rule: " + simpleGenerated);
		
		Optional<Node> simpleLexedNode = simpleClassRule.lex(simpleGenerated);
		if (simpleLexedNode.isPresent()) {
			Node node = simpleLexedNode.get();
			System.out.println("Lexed name: " + node.findString("name").orElse(""));
			System.out.println("Lexed body: " + node.findString("body").orElse(""));
		} else {
			System.out.println("Failed to lex with simple rule");
		}
	}
	
	private static void testStringRule() {
		System.out.println("\n--- Testing StringRule ---");
		Rule rule = new StringRule("test");
		
		// Create a test node
		Node node = new MapNode().withString("test", "Hello World");
		
		// Generate
		String generated = rule.generate(node).orElse("");
		System.out.println("Generated: " + generated);
		
		// Lex
		Optional<Node> lexedNode = rule.lex(generated);
		if (lexedNode.isPresent()) {
			System.out.println("Lexed value: " + lexedNode.get().findString("test").orElse(""));
		} else {
			System.out.println("Failed to lex");
		}
	}
	
	private static void testPrefixRule() {
		System.out.println("\n--- Testing PrefixRule ---");
		Rule innerRule = new StringRule("test");
		Rule rule = new PrefixRule(innerRule, "PREFIX_");
		
		// Create a test node
		Node node = new MapNode().withString("test", "Hello World");
		
		// Generate
		String generated = rule.generate(node).orElse("");
		System.out.println("Generated: " + generated);
		
		// Lex
		Optional<Node> lexedNode = rule.lex(generated);
		if (lexedNode.isPresent()) {
			System.out.println("Lexed value: " + lexedNode.get().findString("test").orElse(""));
		} else {
			System.out.println("Failed to lex");
		}
	}
	
	private static void testSuffixRule() {
		System.out.println("\n--- Testing SuffixRule ---");
		Rule innerRule = new StringRule("test");
		Rule rule = new SuffixRule(innerRule, "_SUFFIX");
		
		// Create a test node
		Node node = new MapNode().withString("test", "Hello World");
		
		// Generate
		String generated = rule.generate(node).orElse("");
		System.out.println("Generated: " + generated);
		
		// Lex
		Optional<Node> lexedNode = rule.lex(generated);
		if (lexedNode.isPresent()) {
			System.out.println("Lexed value: " + lexedNode.get().findString("test").orElse(""));
		} else {
			System.out.println("Failed to lex");
		}
	}
	
	private static void testPlaceholderRule() {
		System.out.println("\n--- Testing PlaceholderRule ---");
		Rule innerRule = new StringRule("test");
		Rule rule = new PlaceholderRule(innerRule);
		
		// Create a test node
		Node node = new MapNode().withString("test", "Hello World");
		
		// Generate
		String generated = rule.generate(node).orElse("");
		System.out.println("Generated: " + generated);
		
		// Lex
		Optional<Node> lexedNode = rule.lex(generated);
		if (lexedNode.isPresent()) {
			System.out.println("Lexed value: " + lexedNode.get().findString("test").orElse(""));
		} else {
			System.out.println("Failed to lex");
		}
	}
	
	private static void testInfixRule() {
		System.out.println("\n--- Testing InfixRule ---");
		Rule leftRule = new StringRule("left");
		Rule rightRule = new StringRule("right");
		Rule rule = new InfixRule(leftRule, rightRule, " INFIX ");
		
		// Create a test node
		Node node = new MapNode()
			.withString("left", "Left Part")
			.withString("right", "Right Part");
		
		// Generate
		String generated = rule.generate(node).orElse("");
		System.out.println("Generated: " + generated);
		
		// Lex
		Optional<Node> lexedNode = rule.lex(generated);
		if (lexedNode.isPresent()) {
			Node lexed = lexedNode.get();
			System.out.println("Lexed left: " + lexed.findString("left").orElse(""));
			System.out.println("Lexed right: " + lexed.findString("right").orElse(""));
		} else {
			System.out.println("Failed to lex");
		}
	}
}*/}