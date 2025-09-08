/*public */struct Main {};
/*public static*/ void main(/*String*/* args){/*
		final var sourceDirectory = Paths.get(".", "src", "java");*//*
		try (var stream = Files.walk(sourceDirectory)) {
			final var sources = stream.filter(Files::isRegularFile)
																.filter(path -> path.toString().endsWith(".java"))
																.collect(Collectors.toSet());

			for (var source : sources) {
				final var input = Files.readString(source);

				final var relative = sourceDirectory.relativize(source);
				final var parent = relative.getParent();
				final var segments = new ArrayList<String>();
				for (var i = 0; i < parent.getNameCount(); i++) {
					segments.add(parent.getName(i).toString());
				}

				var targetDirectory = Paths.get(".", "src", "windows");
				for (var segment : segments) {
					targetDirectory = targetDirectory.resolve(segment);
				}

				Files.createDirectories(targetDirectory);

				final var fileName = relative.getFileName().toString();
				final var index = fileName.lastIndexOf(".");
				final var name = fileName.substring(0, index);
				final var resolve = targetDirectory.resolve(name + ".c");
				Files.writeString(resolve, compile(input));
			}
		}*//* catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}*//*
	*/}
/*private static*/ /*String*/ compile(/*String*/ input){/*
		return compileSegments(input, Main::compileRootSegment);*//*
	*/}
/*private static*/ /*String*/ compileSegments(/*String input, Function<String,*/ /*String>*/ mapper){/*
		final var segments = new ArrayList<String>();*//*
		var buffer = new StringBuilder();*//*
		var depth = 0;*//*
		for (var i = 0;*//* i < input.length();*//* i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer.setLength(0);
				continue;
			}
			if (c == '}*//*' & depth == 1) {
				segments.add(buffer.toString());
				buffer.setLength(0);
				depth--;
				continue;
			}*//*
			if (c == '{') depth++;
			if (c == '}*//*') depth--;*//*
		*/}
/*
		segments.add(buffer.toString());*//*

		return segments.stream().map(mapper).collect(Collectors.joining());*//*
	}*//*private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}*//*private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";

		final var classIndex = stripped.indexOf("*/struct ");
		if (classIndex >= 0) {};
/*
			final var modifiers = stripped.substring(0, classIndex);*//*
			final var remainder = stripped.substring(classIndex + "class ".length());*//*
			final var i = remainder.indexOf("{");
			if (i >= 0) {
				final var name = remainder.substring(0, i).strip();
				final var content = remainder.substring(i + "{".length());
				return wrap(modifiers) + "struct " + name + " {};" + System.lineSeparator() +
							 compileSegments(content, Main::compileClassSegment);
			}
		}

		return wrap(stripped);
	}*//*private static*/ /*String*/ compileClassSegment(/*String*/ input){/*
		final var i = input.indexOf("(");*//*
		if (i >= 0) {
			final var definition = input.substring(0, i);
			final var withParams = input.substring(i + "(".length());
			final var i1 = withParams.indexOf(")");
			if (i1 >= 0) {
				final var params = withParams.substring(0, i1);
				final var content = withParams.substring(i1 + ")".length()).strip();
				if (content.startsWith("{") && content.endsWith("}")) {
					final var slice = content.substring(1, content.length() - 1);
					final var s = compileDefinition(definition);
					if (s.isPresent()) {
						return s.get() + "(" + compileDefinition(params).orElseGet(() -> wrap(params)) + "){" +
									 compileSegments(slice, Main::compileFunctionSegment) + "}" + System.lineSeparator();
					}
				}
			}
		}*//*

		return wrap(input);*//*
	*/}
/*private static*/ /*Optional<String>*/ compileDefinition(/*String*/ input){/*
		final var stripped = input.strip();*//*
		final var i = stripped.lastIndexOf(" ");*//*
		if (i < 0) return Optional.empty();*//*

		final var beforeName = stripped.substring(0, i).strip();*//*
		final var name = stripped.substring(i + " ".length());*//*

		final var i1 = beforeName.lastIndexOf(" ");*//*
		if (i1 < 0) {
			return Optional.of(compileType(beforeName) + " " + name);
		}*//*

		final var modifiers = beforeName.substring(0, i1);*//*
		final var type = beforeName.substring(i1 + " ".length());*//*

		return Optional.of(wrap(modifiers) + " " + compileType(type) + " " + name);*//*
	*/}
/*private static*/ /*String*/ compileType(/*String*/ input){/*
		final var stripped = input.strip();*//*

		if (stripped.equals("void")) return "void";*//*
		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - "[]".length());
			return compileType(slice) + "*";
		}*//*

		return wrap(stripped);*//*
	*/}
/*private static*/ /*String*/ compileFunctionSegment(/*String*/ input){/*
		return wrap(input);*//*
	*/}
/*
}*/