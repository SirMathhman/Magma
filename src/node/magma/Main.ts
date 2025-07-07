/* import java.io.IOException; */
/* import java.nio.file.Files; */
/* import java.nio.file.Path; */
/* import java.nio.file.Paths; */
/* import java.util.List; */
/* import java.util.Optional; */
/* import java.util.function.Function; */
/* import java.util.stream.Collectors; */
/* import java.util.stream.Stream; */
/* public class Main */{
	/* private static final String LINE_SEPARATOR = System.lineSeparator(); */
	/* private Main() */{
	}
	/* public static void main(final String[] args) */{
		/* final var rootDirectory = Paths.get(".", "src", "java"); */
		/* try (final var stream = Files.walk(rootDirectory)) */{
			/* Main.runWithSources(rootDirectory, stream); */
		}
		/* catch (final IOException e) */{
			/* //noinspection CallToPrintStackTrace
            e.printStackTrace(); */
		}
	}
	/* private static void runWithSources(final Path rootDirectory, final Stream<Path> stream) throws IOException */{
		/* final var sources = stream.filter(Files::isRegularFile)
                                  .filter(path -> path.toString().endsWith(".java"))
                                  .collect(Collectors.toSet()); */
		/* for (final var source : sources) Main.runWithSource(rootDirectory, source); */
	}
	/* private static void runWithSource(final Path rootDirectory, final Path source) throws IOException */{
		/* final var relative = rootDirectory.relativize(source.getParent()); */
		/* final var targetParent = Paths.get(".", "src", "node").resolve(relative); */
		/* final var fileName = source.getFileName().toString(); */
		/* final var separator = fileName.lastIndexOf('.'); */
		/* final var name = fileName.substring(0, separator); */
		/* if (!Files.exists(targetParent)) Files.createDirectories(targetParent); */
		/* final var target = targetParent.resolve(name + ".ts"); */
		/* final var input = Files.readString(source); */
		/* Files.writeString(target, Main.compile(input)); */
	}
	/* private static String compile(final CharSequence input) */{
		/* return Main.compileStatements(input, Main::compileRootSegment); */
	}
	/* private static String compileStatements(final CharSequence input, final Function<String, String> mapper) */{
		/* final var segments = Main.divide(input); */
		/* final var output = new StringBuilder(); */
		/* for (final var segment : segments) output.append(mapper.apply(segment)); */
		/* return output.toString(); */
	}
	/* private static List<String> divide(final CharSequence input) */{
		/* DivideState current = new MutableDivideState(); */
		/* for (var i = 0; */
		/* i < input.length(); */
		/* i++) */{
			/* final var c = input.charAt(i); */
			/* current = Main.fold(current, c); */
		}
		/* return current.advance().stream().toList(); */
	}
	/* private static DivideState fold(final DivideState state, final char c) */{
		/* final var appended = state.append(c); */
		/* if ('; */
		/* ' == c && appended.isLevel()) return appended.advance(); */
		/* if (' */
	}
	/* ' == c && appended.isShallow()) return appended.exit().advance(); */
	/* if (' */{
		/* ' == c) return appended.enter(); */
		/* if (' */
	}
	/* ' == c) return appended.exit(); */
	/* return appended; */
	/*  */
}
/* private static String compileRootSegment(final String input) */{
	/* final var strip = input.strip(); */
	/* if (strip.startsWith("package ")) return ""; */
	/* return Main.compileRootSegmentValue(strip) + Main.LINE_SEPARATOR; */
	/*  */
}
/* private static String compileRootSegmentValue(final String input) */{
	/* return Main.compileStructure(input).orElseGet(() -> Main.generatePlaceholder(input)); */
	/*  */
}
/* private static Optional<String> compileStructure(final String input) */{
	/* if (input.isEmpty() || ' */
}
/* ' != input.charAt(input.length() - 1)) return Optional.empty(); */
/* final var substring = input.substring(0, input.length() - "}".length());

        final var i = substring.indexOf('{'); */
/* if (0 > i) return Optional.empty(); */
/* final var substring1 = substring.substring(0, i).strip(); */
/* final var substring2 = substring.substring(i + " */{
	/* ".length()); */
	/* return Optional.of(Main.generatePlaceholder(substring1) + " */{
		/* " +
                           Main.compileStatements(substring2, Main::compileClassSegment) + Main.LINE_SEPARATOR + " */
	}
	/* "); */
	/*  */
}
/* private static String compileClassSegment(final String input) */{
	/* return Main.LINE_SEPARATOR + "\t" + Main.compileClassSegmentValue(input.strip()); */
	/*  */
}
/* private static String compileClassSegmentValue(final String input) */{
	/* return Main.compileMethod(input).orElseGet(() -> Main.generatePlaceholder(input)); */
	/*  */
}
/* private static Optional<String> compileMethod(final String input) */{
	/* if (input.isEmpty() || ' */
}
/* ' != input.charAt(input.length() - 1)) return Optional.empty(); */
/* final var substring = input.substring(0, input.length() - "}".length());

        final var i = substring.indexOf('{'); */
/* if (0 > i) return Optional.empty(); */
/* final var substring1 = substring.substring(0, i).strip(); */
/* final var substring2 = substring.substring(i + " */{
	/* ".length()); */
	/* return Optional.of(Main.generatePlaceholder(substring1) + " */{
		/* " +
                           Main.compileStatements(substring2, input1 -> Main.compileFunctionSegment(input1, 2)) +
                           Main.LINE_SEPARATOR + "\t */
	}
	/* "); */
	/*  */
}
/* private static String compileFunctionSegment(final String input, final int depth) */{
	/* final var strip = input.strip(); */
	/* if (strip.isEmpty()) return ""; */
	/* return Main.LINE_SEPARATOR + "\t".repeat(depth) + Main.compileFunctionSegmentValue(strip, depth); */
	/*  */
}
/* private static String compileFunctionSegmentValue(final String input, final int depth) */{
	/* return Main.compileBlock(input, depth).orElseGet(() -> Main.generatePlaceholder(input)); */
	/*  */
}
/* private static Optional<String> compileBlock(final String input, final int depth) */{
	/* if (input.isEmpty() || ' */
}
/* ' != input.charAt(input.length() - 1)) return Optional.empty(); */
/* final var substring = input.substring(0, input.length() - "}".length());
        final var i = substring.indexOf('{'); */
/* if (0 > i) return Optional.empty(); */
/* final var substring1 = substring.substring(0, i).strip(); */
/* final var substring2 = substring.substring(i + " */{
	/* ".length()).strip(); */
	/* final var generated =
                Main.generatePlaceholder(substring1) + " */{
		/* " + Main.compileFunctionSegments(depth, substring2) +
                Main.LINE_SEPARATOR + "\t".repeat(depth) + " */
	}
	/* "; */
	/* return Optional.of(generated); */
	/*  */
}
/* private static String compileFunctionSegments(final int depth, final CharSequence input) */{
	/* return Main.compileStatements(input, segment -> Main.compileFunctionSegment(segment, depth + 1)); */
	/*  */
}
/* private static String generatePlaceholder(final String input) */{
	/* final var replaced = input.replace("start", "start").replace("end", "end"); */
	/* return "start " + replaced + " end"; */
	/*  */
}
/* } */
