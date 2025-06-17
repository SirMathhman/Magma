package magma;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.rule.EmptyRule;
import magma.app.rule.InfixRule;
import magma.app.rule.OrRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;
import magma.app.rule.divide.DivideRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        Result<StringBuilder, ApplicationError> maybeCurrentOutput = new Ok<>(new StringBuilder());
        for (var source : sources)
            maybeCurrentOutput = maybeCurrentOutput.flatMap(currentOutput -> compileSource(sourceDirectory, source).mapValue(currentOutput::append));

        return maybeCurrentOutput.match(currentOutput -> {
            final var target = Paths.get(".", "diagram.puml");
            final var content = "@startuml\nskinparam linetype ortho\n" + currentOutput + "@enduml";
            return writeString(target, content).map(ThrowableError::new)
                    .map(ApplicationError::new);
        }, Optional::of);
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

    private static Result<String, ApplicationError> compileSource(Path sourceDirectory, Path source) {
        final var relativeParent = sourceDirectory.relativize(source)
                .getParent();

        final Collection<String> namespace = new ArrayList<>();
        for (var i = 0; i < relativeParent.getNameCount(); i++)
            namespace.add(relativeParent.getName(i)
                    .toString());

        final var fileName = source.getFileName()
                .toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        final var joined = String.join(".", namespace);
        return readString(source).mapErr(ThrowableError::new)
                .mapErr(ApplicationError::new)
                .flatMap(input -> {
                    final var joinedName = joined + "." + name;
                    return compileRoot(input, joinedName).mapValue(output -> "class " + joinedName + "\n" + output)
                            .mapErr(ApplicationError::new);
                });
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
                .flatMap(createPlantRootRule()::generate);
    }

    private static Rule createJavaRootRule() {
        return new DivideRule("children", new OrRule(List.of(createNamespacedRule("package"), createNamespacedRule("import"), new StringRule("value"))));
    }

    private static Rule createPlantRootRule() {
        return new DivideRule("children", new OrRule(List.of(createDependencyRule(), new EmptyRule())));
    }

    private static Node transform(String source, Node root) {
        final var transformed = root.findNodeList("children")
                .orElse(new ArrayList<>())
                .stream()
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
