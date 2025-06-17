package magma;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
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
import magma.app.io.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Compiler {
    static Result<String, ApplicationError> compileSource(Source source, String input) {
        final var namespace = source.computeNamespace();
        final var name = source.computeName();

        final var joined = String.join(".", namespace);
        final var joinedName = joined + "." + name;
        return compileRoot(input, joinedName).mapValue(output -> "class " + joinedName + "\n" + output)
                .mapErr(ApplicationError::new);
    }

    static Result<String, FormattedError> compileRoot(String input, String source) {
        return createJavaRootRule().lex(input)
                .mapValue(children -> transform(source, children))
                .flatMapValue(createPlantRootRule()::generate);
    }

    static Rule createJavaRootRule() {
        return new DivideRule("children",
                new OrRule(List.of(createNamespacedRule("package"),
                        new TypeRule("import", createNamespacedRule("import")),
                        createStructureRule("class"),
                        createStructureRule("interface"),
                        createStructureRule("record"))));
    }

    static Rule createStructureRule(String type) {
        return new InfixRule(new StringRule("before-infix"), type + " ", new StringRule("after-infix"));
    }

    static Rule createPlantRootRule() {
        return new DivideRule("children", new OrRule(List.of(createDependencyRule())));
    }

    static Node transform(String source, Node root) {
        final var transformed = root.findNodeList("children")
                .orElse(new ArrayList<>())
                .stream()
                .filter(node -> node.is("import"))
                .map(node -> node.withString("source", source))
                .toList();

        final Node node = new MapNode();
        return node.withNodeList("children", transformed);
    }

    static Rule createNamespacedRule(String type) {
        return new StripRule(new PrefixRule(type + " ", new SuffixRule(new StringRule("destination"), ";")));
    }

    static Rule createDependencyRule() {
        return new SuffixRule(new InfixRule(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    Result<String, ApplicationError> compile(Map<Source, String> inputs) {
        Result<StringBuilder, ApplicationError> maybeCurrentOutput = new Ok<>(new StringBuilder());
        for (var entry : inputs.entrySet())
            maybeCurrentOutput = maybeCurrentOutput.flatMapValue(currentOutput -> compileSource(entry.getKey(),
                    entry.getValue()).mapValue(currentOutput::append));

        return maybeCurrentOutput.mapValue(StringBuilder::toString);
    }
}