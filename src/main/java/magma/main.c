/*package magma;*/
/*import magma.compile.Lang;*/
/*import magma.compile.Node;*/
/*import magma.compile.Serialize;*/
/*import magma.compile.error.ApplicationError;*/
/*import magma.compile.error.CompileError;*/
/*import magma.compile.error.ThrowableError;*/
/*import magma.result.Err;*/
/*import magma.result.Ok;*/
/*import magma.result.Result;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.stream.Stream;*/
struct Main {};
/*public static void main(String[] args)  {

		run().ifPresent(error -> System.out.println(error.display()));
	
}*/
/*private static Optional<ApplicationError> run()  {

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
	
}*/
/*private static Optional<IOException> writeString(Path path, String result)  {

		try {
			Files.writeString(path, result);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	
}*/
/*private static Result<String, ThrowableError> readString(Path source)  {

		try {
			return new Ok<>(Files.readString(source));
		} catch (IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	
}*/
/*private static Result<String, CompileError> compile(String input)  {

		return Lang.createJavaRootRule().lex(input).flatMap(Main::transform).flatMap(Lang.createCRootRule()::generate);
	
}*/
/*private static Result<Node, CompileError> transform(Node node)  {

		return switch (Serialize.deserialize(Lang.JavaRoot.class, node)) {
			case Err<Lang.JavaRoot, CompileError> v -> new Err<>(v.error());
			case Ok<Lang.JavaRoot, CompileError> v ->
					getNodeCompileErrorResult(v.value()).flatMap(n -> Serialize.serialize(Lang.CRoot.class, n));
		};
	
}*/
/*private static Result<Lang.CRoot, CompileError> getNodeCompileErrorResult(Lang.JavaRoot value)  {

		final List<Lang.CRootSegment> newChildren = value.children().stream().flatMap(segment -> switch (segment) {
			case Lang.JavaClass javaClass -> flattenClass(javaClass);
			case Lang.Content content -> Stream.of(content);
			case Lang.JavaImport javaImport -> Stream.of(new Lang.Content("import " + javaImport.content() + ";"));
			case Lang.JavaPackage javaPackage -> Stream.of(new Lang.Content("package " + javaPackage.content() + ";"));
		}).toList();
		return new Ok<>(new Lang.CRoot(newChildren));
	
}*/
/*private static Stream<Lang.CRootSegment> flattenClass(Lang.JavaClass clazz)  {

		final Stream<Lang.CRootSegment> nested = clazz.children().stream().flatMap(member -> switch (member) {
			case Lang.JavaClass javaClass -> flattenClass(javaClass);
			case Lang.JavaStruct struct -> Stream.of(new Lang.CStructure(struct.name()));
			case Lang.Content content -> Stream.of(content);
			case Lang.JavaBlock javaBlock ->
					Stream.of(new Lang.Content(javaBlock.header() + " {\n" + javaBlock.content() + "\n}"));
		});

		return Stream.concat(Stream.of(new Lang.CStructure(clazz.name())), nested);
	
}*/
/**/
/**/
