/*private static*/struct Lists {
};
/*private*/ /*boolean*/ isLevel(/**/) {
}
/*private*/ /*State*/ append(/*char c*/) {
}
/*private*/ /*State*/ advance(/**/) {
}
/*private*/ /*State*/ enter(/**/) {
}
/*private*/ /*State*/ exit(/**/) {
}
/*public*/ /*boolean*/ isShallow(/**/) {
}
/*private static*/struct State {
	/*private*/ /*List<String>*/ segments;
	/*private*/ /*StringBuilder*/ buffer;
	/*private*/ /*int*/ depth;/*

        private State(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }*//*

        public State() {
            this(Lists.empty(), new StringBuilder(), 0);
        }*/
	/*private*/ /*boolean*/ isLevel(/**/);
	/*private*/ /*State*/ append(/*char c*/);
	/*private*/ /*State*/ advance(/**/);
	/*private*/ /*State*/ enter(/**/);
	/*private*/ /*State*/ exit(/**/);
	/*public*/ /*boolean*/ isShallow(/**/);
};
/*private*/ /*String*/ generate(/**/) {
}
/*private*/struct ClassDefinition {
	/*private*/ /*String*/ generate(/**/);
};
/*private*/ /*String*/ generate(/**/) {
}
/*private*/struct JavaDefinition {
	/*private*/ /*String*/ generate(/**/);
};
/*public static*/ /*void*/ main(/*String[] args*/) {
}
/*private static*/ /*String*/ compile(/*String input*/) {
}
/*private static*/ /*String*/ compileStatements(/*String input, Function<String, String> mapper*/) {
}
/*private static*/ /*List<String>*/ divideStatements(/*String input*/) {
}
/*private static*/ /*List<String>*/ divide(/*String input, BiFunction<State, Character, State> folder*/) {
}
/*private static*/ /*State*/ foldStatements(/*State state, char c*/) {
}
/*if*/ (/*c == '{'*/) {
}
/*public*/struct Main {
	/*public static*/ /*void*/ main(/*String[] args*/);
	/*private static*/ /*String*/ compile(/*String input*/);
	/*private static*/ /*String*/ compileStatements(/*String input, Function<String, String> mapper*/);
	/*private static*/ /*List<String>*/ divideStatements(/*String input*/);
	/*private static*/ /*List<String>*/ divide(/*String input, BiFunction<State, Character, State> folder*/);
	/*private static*/ /*State*/ foldStatements(/*State state, char c*/);/*' && appended.isShallow()) {
            return appended.advance().exit();
        }*/
	/*if*/ (/*c == '{'*/);/*') {
            return appended.exit();
        }*/
	/*return*/ appended;
};
/*

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileClass(input)
                .map(tuple -> {
                    final var joined = tuple.left
                            .iter()
                            .collect(new Joiner())
                            .orElse("");

                    return joined + tuple.right;
                })
                .orElseGet(() -> generatePlaceholder(input));
    }*//*


    private static Optional<Tuple<List<String>, String>> compileClass(String input) {
        final var contentStart = input.indexOf('{');
        if (contentStart >= 0) {
            final var beforeContent = input.substring(0, contentStart);
            final var withEnd = input.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                final var maybeHeader = compileClassDefinition(beforeContent);
                if (maybeHeader.isPresent()) {
                    final var definition = maybeHeader.get();
                    final var others = compileClassWithDefinition(definition, withEnd);
                    return Optional.of(new Tuple<>(others, ""));
                }
            }
        }

        return Optional.empty();
    }

    private static List<String> compileClassWithDefinition(ClassDefinition definition, String withEnd) {
        if (definition.typeParameters.isEmpty()) {
            final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());

            final var segments = divideStatements(inputContent);

            final var tuple = segments.iter()
                    .map(Main::compileClassSegment)
                    .collect(new TupleCollector<>(new ListBulkCollector<>(), new Joiner()));

            final var others = tuple.left;
            final var output = tuple.right.orElse("");

            final var generatedHeader = definition.generate();
            final var generated = generatedHeader + " {" + output + "\n};\n";
            return others.addLast(generated);
        }

        return Lists.empty();
    }*//*

    private static Tuple<List<String>, String> compileClassSegment(String input) {
        return compileWhitespace(input)
                .or(() -> compileField(input))
                .or(() -> compileClass(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(Lists.empty(), generatePlaceholder(input)));
    }*//*

    private static Optional<Tuple<List<String>, String>> compileMethod(String input) {
        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var beforeParams = input.substring(0, paramStart);
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var params = withParams.substring(0, paramEnd);
                final var content = withParams.substring(paramEnd + ")".length()).strip();
                final var maybeDefinition = parseDefinition(beforeParams);
                if (maybeDefinition.isPresent()) {
                    final var definition = maybeDefinition.get();
                    if (!definition.typeParameters.isEmpty()) {
                        return Optional.of(new Tuple<>(Lists.empty(), ""));
                    }

                    final var header = definition.generate() + "(" + generatePlaceholder(params) + ")";
                    if (content.equals(";")) {
                        final var generated = header + ";";
                        return Optional.of(new Tuple<>(Lists.of(generated + "\n"), "\n\t" + generated));
                    }

                    return Optional.of(new Tuple<>(Lists.of(header + " {\n}" + "\n"), "\n\t" + header + ";"));
                }
            }
        }

        return Optional.empty();
    }*//*

    private static Optional<Tuple<List<String>, String>> compileWhitespace(String input) {
        if (input.isBlank()) {
            return Optional.of(new Tuple<>(Lists.empty(), ""));
        }
        else {
            return Optional.empty();
        }
    }*//*

    private static Optional<Tuple<List<String>, String>> compileField(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(";")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return parseDefinition(withoutEnd).map(JavaDefinition::generate).map(generated -> {
                return new Tuple<>(Lists.empty(), "\n\t" + generated + ";");
            });
        }

        return Optional.empty();
    }*//*

    private static Optional<JavaDefinition> parseDefinition(String input) {
        final var nameSeparator = input.lastIndexOf(" ");
        if (nameSeparator >= 0) {
            final var beforeName = input.substring(0, nameSeparator).strip();
            final var name = input.substring(nameSeparator + " ".length()).strip();

            if (isSymbol(name)) {
                return parseDefinitionWithBeforeType(beforeName, name);
            }
        }
        return Optional.empty();
    }*//*

    private static boolean isSymbol(String input) {
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c)) {
                continue;
            }
            return false;
        }
        return true;
    }*//*

    private static Optional<JavaDefinition> parseDefinitionWithBeforeType(String beforeName, String name) {
        final var typeSeparator = beforeName.lastIndexOf(" ");
        if (typeSeparator < 0) {
            return compileType(beforeName).map(type -> {
                return new JavaDefinition(Optional.empty(), Lists.empty(), type, name);
            });
        }

        final var type = beforeName.substring(typeSeparator + " ".length());
        return compileType(type).map(compiledType -> {
            final var beforeType = beforeName.substring(0, typeSeparator).strip();
            if (beforeType.endsWith(">")) {
                final var withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
                final var typeParametersStart = withoutEnd.indexOf("<");
                if (typeParametersStart >= 0) {
                    final var beforeTypeParameters = withoutEnd.substring(0, typeParametersStart);
                    final var typeParametersString = withoutEnd.substring(typeParametersStart + "<".length());
                    final var typeParameters = parseTypeParameters(typeParametersString);
                    return new JavaDefinition(Optional.of(beforeTypeParameters), typeParameters, compiledType, name);
                }
            }

            return new JavaDefinition(Optional.of(beforeType), Lists.empty(), compiledType, name);
        });
    }*//*

    private static Optional<String> compileType(String type) {
        if (type.equals("private") || type.equals("public")) {
            return Optional.empty();
        }

        return Optional.of(generatePlaceholder(type));
    }*//*

    private static Optional<ClassDefinition> compileClassDefinition(String input) {
        return compileClassDefinitionWithKeyword(input, "class ")
                .or(() -> compileClassDefinitionWithKeyword(input, "interface "))
                .or(() -> compileClassDefinitionWithKeyword(input, "record "));
    }*//*

    private static Optional<ClassDefinition> compileClassDefinitionWithKeyword(String input, String keyword) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return Optional.empty();
        }

        final var beforeKeyword = input.substring(0, classIndex).strip();
        final var afterKeyword = input.substring(classIndex + keyword.length()).strip();
        return Optional.of(parseClassDefinitionWithParameters(beforeKeyword, afterKeyword));
    }*//*

    private static ClassDefinition parseClassDefinitionWithParameters(String beforeKeyword, String afterKeyword) {
        if (afterKeyword.endsWith(")")) {
            final var withoutEnd = afterKeyword.substring(0, afterKeyword.length() - ")".length());
            final var paramStart = withoutEnd.indexOf("(");
            if (paramStart >= 0) {
                final var beforeParameters = withoutEnd.substring(0, paramStart);
                final var parameters = withoutEnd.substring(paramStart + "(".length());
                return parseClassDefinitionWithTypeParameters(beforeKeyword, beforeParameters);
            }
        }

        return parseClassDefinitionWithTypeParameters(beforeKeyword, afterKeyword);
    }*//*

    private static ClassDefinition parseClassDefinitionWithTypeParameters(String beforeKeyword, String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(">")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            final var typeParamsStart = withoutEnd.indexOf("<");
            if (typeParamsStart >= 0) {
                final var base = withoutEnd.substring(0, typeParamsStart);
                final var typeParameters = withoutEnd.substring(typeParamsStart + "<".length());
                return new ClassDefinition(beforeKeyword, base, parseTypeParameters(typeParameters));
            }
        }

        return new ClassDefinition(beforeKeyword, stripped, Lists.empty());
    }*//*

    private static List<String> parseTypeParameters(String typeParameters) {
        return divideValues(typeParameters)
                .iter()
                .map(String::strip)
                .collect(new ListCollector<>());
    }*//*

    private static List<String> divideValues(String input) {
        return divide(input, Main::foldValues);
    }*//*

    private static State foldValues(State state, char c) {
        if (c == ',') {
            return state.advance();
        }
        return state.append(c);
    }*//*

    private static String generatePlaceholder(String input) {
        return "start" + input
                .replace("start", "start")
                .replace("end", "end") + "end";
    }*//*
}
*/