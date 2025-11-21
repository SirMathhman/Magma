/*public*/ struct Main {
};
Type toType_PrimitiveType(void* _this){
	PrimitiveType this = *((PrimitiveType*) _this);
	TypeData data;
	data.primitivetype = this;
	return { TypeVariant.PrimitiveTypeVariant, data };
}
/*private*/ struct PrimitiveType {
};
PrimitiveType PrimitiveTypeVoid = new_PrimitiveType("void");
enum ResultVariant {
	ErrVariant, 
	OkVariant
};
template <typename T, typename X>
union ResultData {
	ErrData<T, X> err;
	OkData<T, X> ok;
};
template <typename T, typename X>
/*private*/ struct Result {
	ResultVariant variant;
	ResultData data;
};
enum TypeVariant {
	IdentifierVariant, 
	PlaceholderVariant, 
	PointerTypeVariant, 
	PrimitiveTypeVariant, 
	TemplateTypeVariant
};
union TypeData {
	IdentifierData identifier;
	PlaceholderData placeholder;
	PointerTypeData pointertype;
	PrimitiveTypeData primitivetype;
	TemplateTypeData templatetype;
};
/*private*/ struct Type {
	TypeVariant variant;
	TypeData data;
};
/*private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure("class", stripped).orElseGet(() -> wrap(stripped));
	}*//*private static Optional<String> compileStructure(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) {return Optional.empty();}
		final var modifiers = stripped.substring(0, i).strip();
		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) {return Optional.empty();}
		var beforeContent = afterKeyword.substring(0, i1).strip();
		final var content = afterKeyword.substring(i1 + 1);

		List<String> variants = new ArrayList<String>();
		final var i2 = beforeContent.indexOf("permits ");
		if (i2 >= 0) {
			final var substring1 = beforeContent.substring(i2 + "permits ".length());
			beforeContent = beforeContent.substring(0, i2);

			variants = splitValues(substring1);
		}

		List<Type> implementees = new ArrayList<Type>();
		final var i4 = beforeContent.indexOf("implements ");
		if (i4 >= 0) {
			final var implementeesString = beforeContent.substring(i4 + "implements ".length());
			beforeContent = beforeContent.substring(0, i4);

			implementees = divide(implementeesString, Main::foldValue)
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(Main::parseType)
					.toList();
		}

		List<String> typeParameters = new ArrayList<String>();
		final var i3 = beforeContent.indexOf("<");
		if (i3 >= 0) {
			final var substring1 = beforeContent.substring(i3 + 1).strip();
			beforeContent = beforeContent.substring(0, i3);
			if (substring1.endsWith(">")) {
				final var substring = substring1.substring(0, substring1.length() - 1);
				typeParameters = splitValues(substring);
			}
		}

		if (!isIdentifier(beforeContent)) {return Optional.empty();}

		final var modifiersList = Arrays
				.stream(modifiers.split(Pattern.quote(" ")))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));

		var name = beforeContent.strip();

		final var templateString = generateTemplateString(typeParameters);

		final String fields;
		var dependencies = new StringBuilder();
		for (var implementee : implementees) {
			final var identifier = implementee.toIdentifier();

			final var variant = identifier + "Variant" + "." + name + "Variant";
			final var conversionFunctionContent =
					generateStatement(name + " this = *((" + name + "*) _this)") + generateStatement(identifier + "Data data") +
					generateStatement("data." + name.toLowerCase() + " = this") +
					generateStatement("return { " + variant + ", data }");

			final var conversionFunction =
					implementee.generate() + " to" + identifier + "_" + name + "(void* _this){" + conversionFunctionContent +
					System.lineSeparator() + "}" + System.lineSeparator();

			dependencies.append(conversionFunction);
		}

		if (!variants.isEmpty() && modifiersList.contains("sealed")) {
			modifiersList.remove("sealed");

			final var enumFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Variant")
					.collect(Collectors.joining(", "));

			final var generatedEnum =
					"enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

			final String joinedTypeParameters;
			if (typeParameters.isEmpty()) {
				joinedTypeParameters = "";
			} else {
				joinedTypeParameters = typeParameters.stream().collect(Collectors.joining(", ", "<", ">"));
			}

			final var unionFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Data" + joinedTypeParameters + " " +
													variant.toLowerCase() + ";")
					.collect(Collectors.joining());

			final var generatedUnion =
					templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" +
					System.lineSeparator();

			fields = System.lineSeparator() + "\t" + name + "Variant variant;" + System.lineSeparator() + "\t" + name +
							 "Data data;";

			dependencies.append(generatedEnum).append(generatedUnion);
		} else {
			fields = "";
		}

		final String joinedModifiers;
		if (modifiersList.isEmpty()) {
			joinedModifiers = "";
		} else {
			joinedModifiers =
					modifiersList.stream().map(Main::wrap).map(modifier -> modifier + " ").collect(Collectors.joining());
		}

		var finalTypeParameters = typeParameters;
		return Optional.of(
				dependencies + templateString + joinedModifiers + "struct " + name + " {" + fields + System.lineSeparator() +
				"};" + System.lineSeparator() +
				compileStatements(content, input1 -> compileClassSegment(input1, name, finalTypeParameters)));
	}

	private static String generateStatement(String content) {
		return System.lineSeparator() + "\t" + content + ";";
	}

	private static List<String> splitValues(String input) {
		return Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
	}

	private static String generateTemplateString(List<String> typeParameters) {
		final String templateString;
		if (typeParameters.isEmpty()) {
			templateString = "";
		} else {
			templateString = "template " + typeParameters
					.stream()
					.map(typeParam -> "typename " + typeParam)
					.collect(Collectors.joining(", ", "<", ">")) + System.lineSeparator();
		}
		return templateString;
	}

	private static boolean isIdentifier(String input) {
		final var stripped = input.strip();
		for (var i = 0; i < stripped.length(); i++) {
			final var c = stripped.charAt(i);
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}

	private static String compileClassSegment(String input, String structName, List<String> typeParameters) {
		final var stripped = input.strip();

		final var maybeEnum = compileStructure("enum", input);
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		final var maybeInterface = compileStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeEnumValues = compileEnumValues(input, structName);
		if (maybeEnumValues.isPresent()) {
			return maybeEnumValues.get();
		}

		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var declaration = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var parameters = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();

				final var compiledParameters = divide(parameters, Main::foldValue)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList()
						.stream()
						.map(param -> compileDeclaration(param, structName, typeParameters))
						.collect(Collectors.joining(", "));

				final var header = compileDeclaration(declaration, structName, typeParameters) + "(" + compiledParameters +
													 ")";

				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);

					return header + "{" + compileStatements(content, Main::compileMethodSegment) + System.lineSeparator() + "}" +
								 System.lineSeparator();
				} else {
					return header + ";" + System.lineSeparator();
				}
			}
		}

		return wrap(stripped);
	}

	private static Optional<String> compileEnumValues(String input, String structName) {
		final var enumValues =
				divide(input, Main::foldValue).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

		final var buffer = new StringBuilder();
		if (!enumValues.isEmpty()) {
			for (var enumValue : enumValues) {
				if (enumValue.endsWith(")")) {
					final var substring = enumValue.substring(0, enumValue.length() - 1);
					final var i = substring.indexOf("(");
					if (i >= 0) {
						final var name = substring.substring(0, i);
						if (!isIdentifier(name)) {
							return Optional.empty();
						}

						final var substring2 = substring.substring(i + 1);
						buffer.append(
								structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
								System.lineSeparator());
					}
				}
			}
		}

		return Optional.of(buffer.toString());
	}

	private static State foldValue(State state, Character next) {
		if (next == ',' && state.isLevel()) {
			return state.advance();
		}

		final var appended = state.append(next);
		if (next == '<') {
			return appended.enter();
		}
		if (next == '>') {
			return appended.exit();
		}
		return appended;
	}

	private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		return System.lineSeparator() + "\t" + wrap(stripped);
	}

	private static String compileDeclaration(String input, String structName, List<String> typeParameters) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator).strip();
			final var name = stripped.substring(nameSeparator + 1).strip();

			var typeSeparator = -1;
			var depth = 0;
			for (var i = 0; i < beforeName.length(); i++) {
				final var c = beforeName.charAt(i);
				if (c == ' ' && depth == 0) {
					typeSeparator = i;
				}
				if (c == '<') {
					depth++;
				}
				if (c == '>') {
					depth--;
				}
			}

			if (typeSeparator < 0 && isIdentifier(name)) {
				return compileType(beforeName) + " " + name;
			}

			var beforeType = beforeName.substring(0, typeSeparator).strip();

			final var copy = new ArrayList<String>(typeParameters);
			if (beforeType.endsWith(">")) {
				final var substring = beforeType.substring(0, beforeType.length() - 1);
				final var i = substring.indexOf("<");
				if (i >= 0) {
					final var substring2 = substring.substring(i + 1);
					copy.addAll(splitValues(substring2));

					beforeType = substring.substring(0, i);
				}
			}

			var beforeDeclaration = generateTemplateString(copy);

			final var typeString = beforeName.substring(typeSeparator + 1);
			final String beforeTypeOutput;
			if (beforeType.isEmpty()) {
				beforeTypeOutput = "";
			} else {
				beforeTypeOutput = wrap(beforeType) + " ";
			}

			if (isIdentifier(name)) {
				return beforeDeclaration + beforeTypeOutput + compileType(typeString) + " " + name + "_" + structName;
			}
		}

		return wrap(stripped);
	}

	private static String compileType(String input) {
		return parseType(input).generate();
	}

	private static Type parseType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return PrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - 2);
			final var type = parseType(slice);
			return new PointerType(type);
		}

		if (stripped.equals("String")) {
			return new PointerType(PrimitiveType.Char);
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var parameters = substring.substring(i + 1);

				final var list = divide(parameters, Main::foldValue).map(Main::parseType).toList();

				return new TemplateType(base, list);
			}
		}

		if (isIdentifier(stripped)) {
			return new Identifier(stripped);
		}

		return new Placeholder(stripped);
	}

	private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}
}*//**/