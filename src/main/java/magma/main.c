struct Main {};/*

	public static void main(String[] args) {
		if (run() instanceof Some<ApplicationError>(
				ApplicationError value
		)) System.out.println(value.display());
	}*//*temp*//*

	private static Optional<IOException> writeString(Path path, String result) {
		try {
			Files.writeString(path, result);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}*//*

	private static Result<String, ThrowableError> readString(Path source) {
		try {
			return new Ok<>(Files.readString(source));
		} catch (IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	}*//*

	private static Result<String, CompileError> compile(String input) {
		return createJavaRootRule().lex(input)
															 .flatMap(node -> Serialize.deserialize(JavaRoot.class, node))
															 .flatMap(Main::transform)
															 .flatMap(cRoot -> Serialize.serialize(CRoot.class, cRoot))
															 .flatMap(createCRootRule()::generate);
	}*//*

	private static Result<CRoot, CompileError> transform(JavaRoot node) {
		return new Ok<>(new CRoot(node.children().stream().flatMap(Main::flattenRootSegment).toList()));
	}*//*

	private static Stream<CRootSegment> flattenRootSegment(JavaRootSegment segment) {
		return switch (segment) {
			case JClass aClass -> {
				final Structure structure = new Structure(aClass.name());
				yield Stream.concat(Stream.of(structure), aClass.children().stream().map(self -> getSelf(self)));
			}
			case Content content -> Stream.of(content);
			default -> Stream.empty();
		};
	}*//*

	private static CRootSegment getSelf(JavaClassMember self) {
		return switch (self) {
			case Content content -> content;
			case Method method -> new Content("temp");
		};
	}*//*
*/