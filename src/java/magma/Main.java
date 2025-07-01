package magma;

import magma.compile.result.ResultFactoryImpl;
import magma.error.ApplicationError;
import magma.error.Error;
import magma.error.FormatError;
import magma.error.ThrowableError;
import magma.lang.JavaLang;
import magma.lang.PlantLang;
import magma.node.EverythingNode;
import magma.node.MapNode;
import magma.node.NodeWithNodeLists;
import magma.node.result.NodeResult;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import magma.rule.DivideRule;
import magma.rule.Rule;
import magma.string.Strings;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Main {
    private Main() {}

    public static void main(final String[] args) {
        Main.collect()
            .mapErr(ThrowableError::new)
            .<Error>mapErr(ApplicationError::new)
            .match(Main::runWithFiles, Optional::of)
            .ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<Error> runWithFiles(final Collection<Path> files) {
        final Collector<Path, ?, Set<Path>> setCollector = Collectors.toSet();
        final var sources = files.stream().filter(Files::isRegularFile).filter(Main::isJavaFile).collect(setCollector);

        return Main.runWithSources(sources).match(output -> {
            final var target = Paths.get(".", "diagram.puml");
            final var joined = String.join(Strings.LINE_SEPARATOR, output);
            return Main.writeString(target, joined).map(ThrowableError::new).map(ApplicationError::new);
        }, Optional::of);
    }

    private static Optional<IOException> writeString(final Path target, final CharSequence output) {
        try {
            Files.writeString(target, output);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(e);
        }
    }

    private static Result<List<Path>, IOException> collect() {
        final var root = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(root)) {
            return new Ok<>(stream.toList());
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static boolean isJavaFile(final Path file) {
        final var asString = file.toString();
        return asString.endsWith(".java");
    }

    private static Result<String, Error> runWithSources(final Iterable<Path> files) {
        final var pre = Stream.of("@startuml", "skinparam linetype ortho")
                              .map(value -> value + Strings.LINE_SEPARATOR)
                              .collect(Collectors.joining());

        Result<String, Error> outputRootSegments = new Ok<>(pre);
        for (final var source : files) outputRootSegments = Main.runWithSource(outputRootSegments, source);

        return outputRootSegments.mapValue(value -> value + "@enduml");
    }

    private static Result<String, Error> runWithSource(final Result<String, Error> maybeCurrent, final Path source) {
        return Main.readString(source)
                   .mapErr(ThrowableError::new)
                   .<Error>mapErr(ApplicationError::new)
                   .flatMapValue(input -> Main.runWithInput(maybeCurrent, source, input));
    }

    private static Result<String, Error> runWithInput(final Result<String, Error> maybeCurrent,
                                                      final Path source,
                                                      final String input) {
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var parent = fileName.substring(0, separator);

        return maybeCurrent.flatMapValue(current -> Main.compile(input, parent)
                                                        .match(output -> new Ok<>(current + output),
                                                               compileError -> new Err<>(
                                                                       new ApplicationError(compileError))));
    }

    private static Result<String, IOException> readString(final Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static StringResult<FormatError> compile(final String input, final String parent) {
        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> everythingNodeRule =
                Main.createJavaRootRule();
        return everythingNodeRule.lex(input).match(root -> {
            final var newChildren = Main.modify(parent, root);
            final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> everythingNodeRule1 =
                    PlantLang.createPlantRootRule();
            return everythingNodeRule1.generate(newChildren);
        }, StringErr::new);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> createJavaRootRule() {
        return new DivideRule<>("children", JavaLang.createJavaRootSegmentRule(), ResultFactoryImpl.get());
    }

    private static EverythingNode modify(final String parent, final NodeWithNodeLists<EverythingNode> root) {
        final var newChildren = root.findNodeList("children")
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .filter(node -> !node.is("placeholder") && !node.is("package"))
                                    .map(child -> Main.modifyRootSegment(parent, child))
                                    .toList();

        final EverythingNode node = new MapNode();
        return node.withNodeList("children", newChildren);
    }

    private static EverythingNode modifyRootSegment(final String parent, final EverythingNode node) {
        if (node.is("import")) return Main.modifyImport(parent, node);
        return Main.modifyStructure(node);
    }

    private static EverythingNode modifyStructure(final EverythingNode structure) {
        final var name = structure.findString("name").orElse("");
        final var typeParameters = structure.findString("type-parameters").map(value -> "<" + value + ">").orElse("");

        final var maybeBase = structure.findString("base").map(value -> " implements " + value).orElse("");
        final var content = name + typeParameters + maybeBase;

        if (structure.is("record")) return structure.retype("class").withString("content", content);
        return structure.withString("content", content);
    }

    private static EverythingNode modifyImport(final String parent, final EverythingNode child1) {
        return child1.retype("dependency").withString("parent", parent);
    }
}
