package magma;

import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.ApplicationError;
import magma.compile.error.CompileError;
import magma.compile.error.ThrowableError;
import magma.compile.rule.DivideRule;
import magma.compile.rule.InfixRule;
import magma.compile.rule.NodeRule;
import magma.compile.rule.OrRule;
import magma.compile.rule.PlaceholderRule;
import magma.compile.rule.PrefixRule;
import magma.compile.rule.Rule;
import magma.compile.rule.StringRule;
import magma.compile.rule.StripRule;
import magma.compile.rule.SuffixRule;
import magma.compile.rule.TypeRule;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
		run().ifPresent(error -> System.out.println(error.display()));
	}

	private static Optional<ApplicationError> run() {
		final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		return switch (readString(source)) {
			case Err<String, ThrowableError>(ThrowableError error) -> Optional.of(new ApplicationError(error));
			case Ok<String, ThrowableError>(String input) -> {
				final Result<String, CompileError> result = compile(input);
				yield switch (result) {
					case Err<String, CompileError> v -> Optional.of(new ApplicationError(v.error()));
					case Ok<String, CompileError> v -> {
						final Path path = source.resolveSibling("main.c");
						yield writeString(path, v.value()).map(ThrowableError::new).map(ApplicationError::new);
					}
				};
			}
		};
	}

	private static Optional<IOException> writeString(Path path, String result) {
		try {
			Files.writeString(path, result);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}

	private static Result<String, ThrowableError> readString(Path source) {
		try {
			return new Ok<>(Files.readString(source));
		} catch (IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	}

	private static Result<String, CompileError> compile(String input) {
		return createJavaRootRule().lex(input).flatMap(Main::transform).flatMap(createCRootRule()::generate);
	}

	private static Rule createJavaRootRule() {
		return new DivideRule("children", createJavaRootSegmentRule());
	}

	private static Rule createJavaRootSegmentRule() {
		return new StripRule(new OrRule(
				List.of(createClassRule(), createPrefixRule("package"), createPrefixRule("import"), createContentRule())));
	}

	private static Rule createPrefixRule(String type) {
		return new TypeRule(type, new PrefixRule(type + " ", new StringRule("content")));
	}

	private static Result<Node, CompileError> transform(Node node) {
		return switch (Serialize.deserialize(Lang.JavaRoot.class, node)) {
			case Err<Lang.JavaRoot, CompileError> v -> new Err<>(v.error());
			case Ok<Lang.JavaRoot, CompileError> v ->
				getNodeCompileErrorResult(v.value()).flatMap(n -> Serialize.serialize(Lang.CRoot.class, n));
		};
	}

	private static Result<Lang.CRoot, CompileError> getNodeCompileErrorResult(Lang.JavaRoot value) {
		final List<Lang.CRootSegment> newChildren = value.children().stream().flatMap(segment -> switch (segment) {
			case Lang.JavaClass javaClass -> flattenClass(javaClass);
			case Lang.Content content -> Stream.of(content);
			case Lang.JavaImport javaImport -> Stream.of(new Lang.Content("import " + javaImport.content() + ";"));
			case Lang.JavaPackage javaPackage -> Stream.of(new Lang.Content("package " + javaPackage.content() + ";"));
		}).toList();
		return new Ok<>(new Lang.CRoot(newChildren));
	}

	private static Stream<Lang.CRootSegment> flattenClass(Lang.JavaClass clazz) {
		final Stream<Lang.CRootSegment> nested = clazz.children().stream().flatMap(member -> switch (member) {
			case Lang.JavaClass javaClass -> flattenClass(javaClass);
			case Lang.JavaStruct struct -> Stream.of(new Lang.CStructure(struct.name()));
			case Lang.Content content -> Stream.of(content);
			case Lang.JavaBlock javaBlock ->
				Stream.of(new Lang.Content(javaBlock.header() + " {\n" + javaBlock.content() + "\n}"));
		});

		return Stream.concat(Stream.of(new Lang.CStructure(clazz.name())), nested);
	}

	private static Rule createClassRule() {
		final NodeRule header = new NodeRule("header", createClassHeaderRule());
		final DivideRule children = new DivideRule("children", createJavaClassSegmentRule());
		return new TypeRule("class", new SuffixRule(new InfixRule(header, "{", children), "}"));
	}

	private static Rule createCRootRule() {
		return new DivideRule("children", createCRootSegmentRule());
	}

	private static Rule createCRootSegmentRule() {
		return new OrRule(List.of(new SuffixRule(createClassSegmentRule(), System.lineSeparator()), createContentRule()));
	}

	private static Rule createJavaClassSegmentRule() {
		return new StripRule(createClassSegmentRule());
	}

	private static Rule createClassSegmentRule() {
		return new OrRule(List.of(createStructHeaderRule(), createBlockRule(), createContentRule()));
	}

	private static Rule createContentRule() {
		return new TypeRule("content", new PlaceholderRule(new StringRule("input")));
	}

	private static Rule createBlockRule() {
		return new TypeRule("block", new SuffixRule(new InfixRule(new PlaceholderRule(new StringRule("header")), "{",
				new PlaceholderRule(new StringRule("content"))), "}"));
	}

	private static Rule createStructHeaderRule() {
		return new TypeRule("struct", new PrefixRule("struct ", new SuffixRule(new StringRule("name"), " {};")));
	}

	private static Rule createClassHeaderRule() {
		return new InfixRule(new StringRule("temp"), "class ", new StripRule(new StringRule("name")));
	}
}
