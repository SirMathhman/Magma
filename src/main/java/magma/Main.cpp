struct Main {
};
enum ResultTag {
	Err,
	Ok
};
template <typename T, typename X>
union ResultData {
	Err err;
	Ok ok;
};
template <typename T, typename X>
struct Result {
	ResultTag tag;
	ResultData data;
};
/**/struct CPPType {
};
/*String generate();*//*

		String getSimpleName();*//*
	*/template <typename T, typename X>
Result<T, X> toResult_Err(void* _ref){
	Err<T, X> this = *((Err<T, X>*) _ref);
	ResultData data;
	data.err = this;
	return Result<T, X> { Err, data };
}
template <typename T, typename X>
struct Err {
};
/**/template <typename T, typename X>
Result<T, X> toResult_Ok(void* _ref){
	Ok<T, X> this = *((Ok<T, X>*) _ref);
	ResultData data;
	data.ok = this;
	return Result<T, X> { Ok, data };
}
template <typename T, typename X>
struct Ok {
};
/**/CPPType toCPPType_CPointerType(void* _ref){
	CPointerType this = *((CPointerType*) _ref);
	CPPTypeData data;
	data.cpointertype = this;
	return CPPType { CPointerType, data };
}
struct CPointerType {
};
/*@Override
		public*/ char* generate() {/*
			return this.type.generate() + "*";*//*
		*/}/*@Override
		public*/ char* getSimpleName() {/*
			return this.type.getSimpleName() + "_ref";*//*
		*/}/*
	*/CPPType toCPPType_CTemplateType(void* _ref){
	CTemplateType this = *((CTemplateType*) _ref);
	CPPTypeData data;
	data.ctemplatetype = this;
	return CPPType { CTemplateType, data };
}
struct CTemplateType {
};
/*@Override
		public*/ char* generate() {/*
			final var joined = String.join(", ", this.list);*//*
			return this.base + "<" + joined + ">";*//*
		*/}/*@Override
		public*/ char* getSimpleName() {/*
			return this.base;*//*
		*/}/*
	*/CPPType toCPPType_CIdentifier(void* _ref){
	CIdentifier this = *((CIdentifier*) _ref);
	CPPTypeData data;
	data.cidentifier = this;
	return CPPType { CIdentifier, data };
}
struct CIdentifier {
};
/*@Override
		public*/ char* generate() {/*
			return this.input;*//*
		*/}/*@Override
		public*/ char* getSimpleName() {/*
			return this.input;*//*
		*/}/*
	*/CPPType toCPPType_Placeholder(void* _ref){
	Placeholder this = *((Placeholder*) _ref);
	CPPTypeData data;
	data.placeholder = this;
	return CPPType { Placeholder, data };
}
struct Placeholder {
};
/*@Override
		public*/ char* generate() {/*
			return wrap(this.input);*//*
		*/}/*@Override
		public*/ char* getSimpleName() {/*
			return this.generate();*//*
		*/}/*
	*//*public static*/ void main(char** args) {/*
		run().ifPresent(Throwable::printStackTrace);*//*
	*/}/*private static*/ Optional<IOException> run() {/*
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");*//*
		final var input = readString(source);*//*
		return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> compilePath(source, v.value);
		}*//*;*//*
	*/}/*private static*/ Optional<IOException> compilePath(/*Path source,*/ char* input) {/*
		final var target = source.resolveSibling("Main.cpp");*//*
		final var output = compile(input);*//*
		return writeString(target, output).or(() -> compileNative(target));*//*
	*/}/*private static Optional<? extends*/ /*IOException>*/ compileNative(Path target) {/*
		final var clang = startCommand(List.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));*//*
		return switch (clang) {
			case Err<Process, IOException> v1 -> Optional.of(v1.error);
			case Ok<Process, IOException> v1 -> waitForProcess(v1.value);
		}*//*;*//*
	*/}/*private static*/ Optional<IOException> waitForProcess(Process process) {/*
		return switch (waitFor(process)) {
			case Err<Integer, IOException> v2 -> Optional.of(v2.error);
			case Ok<Integer, IOException> v2 -> {
				System.out.println("Compilation failed with exit code: " + v2.value);
				yield Optional.empty();
			}
		}*//*;*//*
	*/}/*private static Result<Integer,*/ /*IOException>*/ waitFor(Process process) {/*
		try {
			return new Ok<Integer, IOException>(process.waitFor());
		}*//* catch (InterruptedException e) {
			return new Err<Integer, IOException>(new IOException(e));
		}*//*
	*/}/*private static Result<Process,*/ /*IOException>*/ startCommand(List<char*> command) {/*
		try {
			return new Ok<Process, IOException>(new ProcessBuilder(command).inheritIO().start());
		}*//* catch (IOException e) {
			return new Err<Process, IOException>(e);
		}*//*
	*/}/*private static*/ Optional<IOException> writeString(/*Path target,*/ char* output) {/*
		try {
			Files.writeString(target, output);
			return Optional.empty();
		}*//* catch (IOException e) {
			return Optional.of(e);
		}*//*
	*/}/*private static Result<String,*/ /*IOException>*/ readString(Path source) {/*
		try {
			return new Ok<String, IOException>(Files.readString(source));
		}*//* catch (IOException e) {
			return new Err<String, IOException>(e);
		}*//*
	*/}/*private static*/ char* compile(char* input) {/*
		return compileStatements(input, Main::compileRootSegment) + "int main(){" + System.lineSeparator() + "\treturn " +
					 "0;" + System.lineSeparator() + "}*//*";*//*
	*/}/*private static*/ char* compileStatements(/*String input, Function<String,*/ /*String>*/ mapper) {/*
		final var segments = new ArrayList<String>();*//*
		var buffer = new StringBuilder();*//*
		var depth = 0;*//*
		for (var i = 0;*//* i < input.length();*//* i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			} else if (c == '}*//*' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				depth--;
			}*//* else {
				if (c == '{') {
					depth++;
				}
				if (c == '}') {
					depth--;
				}
			}*//*
		*/}/*
		segments.add(buffer.toString());*//*

		return segments.stream().map(mapper).collect(Collectors.joining());*//*
	*//*

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure("class", stripped).orElseGet(() -> wrap(input));
	}*/struct Index = input.indexOf {
};
/*final var afterKeyword = input.substring(classIndex + type.length());*/struct Type = maybeInterfaceType.get {
};
/*joinedTypeArguments = "";*//*
						} else {
							joinedTypeArguments = "<" + String.join(", ", typeParameters) + ">";*/struct Type.generate {
};
/*" +
														generateStatement(thisType + " this = *((" + thisType + "*) _ref)") +
														generateStatement(interfaceType.getSimpleName() + "Data data") +
														generateStatement("data." + beforeContent.toLowerCase() + " = this") + generateStatement(
								"return " + interfaceType.generate() + " { " + beforeContent + ", " + "data *//*") +
														System.lineSeparator() + "}" + System.lineSeparator();
					}

					return Optional.of(
							dependencies + templateString + "struct " + beforeContent + " {" + fields + System.lineSeparator() +
							"};" + System.lineSeparator() + compileStatements(content, Main::compileClassSegment));
				}
			}
		*//*

		return Optional.empty();*//*
	}

	private static String generateStatement(String content) {
		return generateWithIndent(content) + ";*//*";*//*
	}

	private static String generateWithIndent(String content) {
		return System.lineSeparator() + "\t" + content;*//*
	}

	private static boolean isIdentifier(String input) {
		for (var i = 0;*//* i < input.length();*//* i++) {
			if (!Character.isLetter(input.charAt(i))) {
				return false;
			}
		}*//*

		return true;*//*
	}

	private static String compileClassSegment(String input) {
		final var maybeInterface = compileStructure("interface", input);*//*
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}*//*

		final var maybeRecord = compileStructure("record ", input);*//*
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}*//*

		final var paramStart = input.indexOf("(");*//*if*/(/*paramStart*/ /*>=*/ 0) {/*
			final var definition = input.substring(0, paramStart).strip();*//*
			final var withParams = input.substring(paramStart + 1);*//*
			final var paramEnd = withParams.indexOf(")");*//*
			if (paramEnd >= 0) {
				final var params = withParams.substring(0, paramEnd).strip();
				final var withBraces = withParams.substring(paramEnd + 1).strip();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);
					return compileDefinition(definition) + "(" + compileParameters(params) + ") {" +
								 compileStatements(content, Main::compileMethodSegment) + "}";
				}
			}*//*
		*/}/*

		return wrap(input);*//*
	}

	private static String compileMethodSegment(String input) {
		return wrap(input);*//*}

	private static*/ char* compileParameters(char* input) {/*
		if (input.isEmpty()) {
			return "";
		*/}/*
		return compileDefinition(input);*//*
	}

	private static String compileDefinition(String input) {
		final var nameSeparator = input.lastIndexOf(" ");*//*if*/(/*nameSeparator*/ /*<*/ 0) {/*
			return wrap(input);*//*
		*/}/*

		final var beforeName = input.substring(0, nameSeparator);*//*
		final var name = input.substring(nameSeparator + 1).strip();*//*
		final var typeSeparator = beforeName.lastIndexOf(" ");*//*if*/(/*typeSeparator*/ /*>=*/ 0) {/*
			final var beforeType = beforeName.substring(0, typeSeparator);*//*
			final var type = beforeName.substring(typeSeparator + 1).strip();*//*
			return wrap(beforeType) + " " + compileType(type).generate() + " " + name;*//*
		*/}/* else {
			return compileType(beforeName).generate() + " " + name;
		}*//*}

	private static*/ CPPType compileType(char* input) {/*
		if (input.equals("void")) {
			return CPPPrimitiveTypes.Void;
		*/}/*

		if (input.endsWith("[]")) {
			final var slice = input.substring(0, input.length() - 2);
			return new CPointerType(compileType(slice));
		}*//*

		if (input.equals("String")) {
			return new CPointerType(CPPPrimitiveTypes.Char);
		}*//*

		if (input.endsWith(">")) {
			final var withoutEnd = input.substring(0, input.length() - 1);
			final var i = withoutEnd.indexOf("<");
			if (i >= 0) {
				final var base = withoutEnd.substring(0, i);
				final var typeArguments = withoutEnd.substring(i + 1);

				final var list = Arrays
						.stream(typeArguments.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(input1 -> compileType(input1).generate())
						.toList();

				return new CTemplateType(base, list);
			}
		}*//*

		if (isIdentifier(input)) {
			return new CIdentifier(input);
		}*//*

		return new Placeholder(input);*//*
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";*//*
	}

	private enum CPPPrimitiveTypes implements CPPType {
		Void("void"), Char("char");*//*

		private final String content;*//*CPPPrimitiveTypes*/(char* content) {/*this.content = content;*//**/}/*@Override
		public*/ char* generate() {/*
			return this.content;*//*
		*/}/*@Override
		public*/ char* getSimpleName() {/*
			return this.content;*//*
		*/}/*
	}
*//*
*/int main(){
	return 0;
}