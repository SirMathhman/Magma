struct Main{};
void main_Main(char** args) {/*
		if (run() instanceof Some<ApplicationError>(ApplicationError value)) System.err.println(value.display());
	*/}
/*Option_?*/ run_Main() {/*
		final Path javaSourceRoot = Paths.get(".", "src", "main", "java");
		final Path cOutputRoot = Paths.get(".", "src", "main", "windows");

		// Ensure output directory exists
		try {
			Files.createDirectories(cOutputRoot);
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}

		return compileAllJavaFiles(javaSourceRoot, cOutputRoot);
	*/}
/*Option_?*/ compileAllJavaFiles_Main(Path javaSourceRoot, Path cOutputRoot) {/*
		try (Stream<Path> paths = Files.walk(javaSourceRoot)) {
			List<Path> javaFiles =
					paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();

			System.out.println("Found " + javaFiles.size() + " Java files to compile");

			for (Path javaFile : javaFiles) {
				System.out.println("Compiling: " + javaFile);
				Option<ApplicationError> result = compileJavaFile(javaFile, javaSourceRoot, cOutputRoot);
				if (result instanceof Some<ApplicationError>(ApplicationError error)) {
					System.err.println("Failed to compile " + javaFile + ": " + error.display());
					return result; // Fail fast - return the error immediately
				}
				System.out.println("Successfully compiled: " + javaFile);
			}

			return Option.empty();
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}
	*/}
/*Option_?*/ compileJavaFile_Main(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {/*
		// Calculate relative path from source root
		Path relativePath = javaSourceRoot.relativize(javaFile);

		// Change extension from .java to .c
		String fileName = relativePath.getFileName().toString();
		String cFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".c";
		Path cFilePath = cOutputRoot.resolve(relativePath.getParent()).resolve(cFileName);

		// Ensure output directory exists
		try {
			Files.createDirectories(cFilePath.getParent());
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}

		Result<String, ThrowableError> readResult = readString(javaFile);
		if (readResult instanceof Err<String, ThrowableError>(ThrowableError error))
			return Option.of(new ApplicationError(error));

		if (readResult instanceof Ok<String, ThrowableError>(String input)) {
			Result<String, CompileError> compileResult = compile(input);
			if (compileResult instanceof Err<String, CompileError>(CompileError error))
				return Option.of(new ApplicationError(error));
			if (compileResult instanceof Ok<String, CompileError>(String compiled))
				return writeString(cFilePath, compiled).map(ThrowableError::new).map(ApplicationError::new);
		}

		return Option.empty();
	*/}
/*Option_?*/ writeString_Main(Path path, char* result) {/*
		try {
			Files.writeString(path, result);
			return Option.empty();
		} catch (IOException e) {
			return Option.of(e);
		}
	*/}
/*ThrowableError>*/ readString_Main(Path source) {/*
		try {
			return new Ok<>(Files.readString(source));
		} catch (IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	*/}
/*CompileError>*/ compile_Main(char* input) {/*
		return JavaRoot().lex(input)
										 .flatMap(node -> Serialize.deserialize(JavaRoot.class, node))
										 .flatMap(Main::transform)
										 .flatMap(cRoot -> Serialize.serialize(CRoot.class, cRoot))
										 .flatMap(CRoot()::generate);
	*/}
/*CompileError>*/ transform_Main(JavaRoot node) {/*
		return new Ok<>(new CRoot(node.children()
																	.stream()
																	.map(Main::flattenRootSegment)
																	.flatMap(Collection::stream)
																	.toList()));
	*/}
/*List_?*/ flattenRootSegment_Main(JavaRootSegment segment) {/*
		return switch (segment) {
			case JStructure jStructure -> flattenStructure(jStructure); case Invalid invalid -> List.of(invalid);
			default -> Collections.emptyList();
		};
	*/}
/*List_?*/ flattenStructure_Main(JStructure aClass) {/*
		final List<JavaStructureSegment> children = aClass.children();

		final ArrayList<CRootSegment> segments = new ArrayList<>();
		final ArrayList<CDefinition> fields = new ArrayList<>();

		final String name = aClass.name();
		for (JavaStructureSegment child : children) {
			final Tuple<List<CRootSegment>, Option<CDefinition>> tuple = flattenStructureSegment(child, name);
			segments.addAll(tuple.left());
			if (tuple.right() instanceof Some<CDefinition>(CDefinition value)) fields.add(value);
		}

		final Structure structure = new Structure(name, fields, new Some<>(System.lineSeparator()));
		final List<CRootSegment> copy = new ArrayList<>();
		copy.add(structure);
		copy.addAll(segments);
		return copy;
	*/}
/*Option_?*/ flattenStructureSegment_Main(JavaStructureSegment self, char* name) {/*
		return switch (self) {
			case Invalid invalid -> new Tuple<>(List.of(invalid), new None<>());
			case Method method -> new Tuple<>(List.of(transformMethod(method, name)), new None<>());
			case Whitespace _ -> new Tuple<>(Collections.emptyList(), new None<>());
			case JStructure jClass -> new Tuple<>(flattenStructure(jClass), new None<>());
			case Field field -> new Tuple<>(Collections.emptyList(), new Some<>(transformDefinition(field.value())));
		};
	*/}
Function transformMethod_Main(Method method, char* structName) {/*
		final List<JavaDefinition> oldParams = switch (method.params()) {
			case None<List<JavaDefinition>> _ -> Collections.emptyList();
			case Some<List<JavaDefinition>> v -> v.value();
		};

		final List<CDefinition> newParams = oldParams.stream().map(Main::transformDefinition).toList();

		final CDefinition cDefinition = transformDefinition(method.definition());
		return new Function(new CDefinition(cDefinition.name() + "_" + structName, cDefinition.type()),
												newParams,
												method.body().orElse(""),
												new Some<>(System.lineSeparator()));
	*/}
CDefinition transformDefinition_Main(JavaDefinition definition) {/*
		return new CDefinition(definition.name(), transformType(definition.type()));
	*/}
CType transformType_Main(JavaType type) {/*
		return switch (type) {
			case Invalid invalid -> invalid;
			case Generic generic -> new Invalid(generic.base() + "_?", new Some<>(System.lineSeparator()));
			case Array array -> {
				CType childType = transformType(array.child()); yield new Pointer(childType);
			} case Identifier identifier -> {
				if (identifier.value().equals("String")) yield new Pointer(new Identifier("char")); yield identifier;
			}
		};
	*/}
