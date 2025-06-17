package magma;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.divide.DivideRule;
import magma.app.error.ApplicationError;
import magma.app.error.ThrowableError;
import magma.app.io.PathSource;
import magma.app.io.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        collect(sourceDirectory).mapErr(ThrowableError::new)
                .mapErr(ApplicationError::new)
                .match(sources -> collect(sources, sourceDirectory), Optional::of)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<ApplicationError> collect(Iterable<Path> sources, Path sourceDirectory) {
        return readAll(sources, sourceDirectory).match(inputs -> {
            return compile(inputs).match(currentOutput -> {
                final var target = Paths.get(".", "diagram.puml");
                final var content = "@startuml\nskinparam linetype ortho\n" + currentOutput + "@enduml";
                return writeString(target, content).map(ThrowableError::new)
                        .map(ApplicationError::new);
            }, Optional::of);
        }, Optional::of);
    }

    private static Result<String, ApplicationError> compile(Map<Source, String> inputs) {
        Result<StringBuilder, ApplicationError> maybeCurrentOutput = new Ok<>(new StringBuilder());
        for (var entry : inputs.entrySet())
            maybeCurrentOutput = maybeCurrentOutput.flatMapValue(currentOutput -> {
                return compileSource(entry.getKey(), entry.getValue()).mapValue(currentOutput::append);
            });

        return maybeCurrentOutput.mapValue(StringBuilder::toString);
    }

    private static Result<Map<Source, String>, ApplicationError> readAll(Iterable<Path> sources, Path sourceDirectory) {
        Result<Map<Source, String>, ApplicationError> maybeInputs = new Ok<>(new HashMap<>());
        for (var source : sources)
            maybeInputs = getMaybeInputs(sourceDirectory, source, maybeInputs);
        return maybeInputs;
    }

    private static Result<Map<Source, String>, ApplicationError> getMaybeInputs(Path sourceDirectory, Path source, Result<Map<Source, String>, ApplicationError> maybeInputs) {
        return maybeInputs.flatMapValue(inner -> readString(source).mapErr(ThrowableError::new)
                .mapErr(ApplicationError::new)
                .mapValue(input -> {
                    inner.put(new PathSource(sourceDirectory, source), input);
                    return inner;
                }));
    }

    private static Result<List<Path>, IOException> collect(Path sourceDirectory) {
        try (var files = Files.walk(sourceDirectory)) {
            final var sources = files.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith("java"))
                    .toList();

            return new Ok<>(sources);
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static Optional<IOException> writeString(Path target, CharSequence content) {
        try {
            Files.writeString(target, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    private static Result<String, ApplicationError> compileSource(Source source, String input) {
        final var namespace = source.computeNamespace();
        final var name = source.computeName();

        final var joined = String.join(".", namespace);
        final var joinedName = joined + "." + name;
        return compileRoot(input, joinedName).mapValue(output -> "class " + joinedName + "\n" + output)
                .mapErr(ApplicationError::new);
    }

    private static Result<String, IOException> readString(Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static Result<String, CompileError> compileRoot(String input, String source) {
        return createJavaRootRule().lex(input)
                .mapValue(children -> transform(source, children))
                .flatMapValue(createPlantRootRule()::generate);
    }

    private static Rule createJavaRootRule() {
        return new DivideRule("children",
                new OrRule(List.of(createNamespacedRule("package"),
                        new TypeRule("import", createNamespacedRule("import")),
                        createStructureRule("class"),
                        createStructureRule("interface"),
                        createStructureRule("record"))));
    }

    private static Rule createStructureRule(String type) {
        return new InfixRule(new StringRule("before-infix"), type + " ", new StringRule("after-infix"));
    }

    private static Rule createPlantRootRule() {
        return new DivideRule("children", new OrRule(List.of(createDependencyRule())));
    }

    private static Node transform(String source, Node root) {
        final var transformed = root.findNodeList("children")
                .orElse(new ArrayList<>())
                .stream()
                .filter(node -> node.is("import"))
                .map(node -> node.withString("source", source))
                .toList();

        final Node node = new MapNode();
        return node.withNodeList("children", transformed);
    }

    private static Rule createNamespacedRule(String type) {
        return new StripRule(new PrefixRule(type + " ", new SuffixRule(new StringRule("destination"), ";")));
    }

    private static Rule createDependencyRule() {
        return new SuffixRule(new InfixRule(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }
}
