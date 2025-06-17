package magma.app.compile;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.Rule;
import magma.app.error.ApplicationError;
import magma.app.io.Source;

import java.util.ArrayList;
import java.util.Map;

public class RuleCompiler implements Compiler {
    private final Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> targetRule;
    private final Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> sourceRule;

    public RuleCompiler(Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> sourceRule, Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> targetRule) {
        this.sourceRule = sourceRule;
        this.targetRule = targetRule;
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

    Result<String, ApplicationError> compileSource(Source source, String input) {
        final var namespace = source.computeNamespace();
        final var name = source.computeName();

        final var joined = String.join(".", namespace);
        final var joinedName = joined + "." + name;
        return this.compileRoot(input, joinedName)
                .mapValue(output -> "class " + joinedName + "\n" + output)
                .mapErr(ApplicationError::new);
    }

    Result<String, FormattedError> compileRoot(String input, String source) {
        return this.sourceRule.lex(input)
                .mapValue(children -> transform(source, children))
                .flatMapValue(this.targetRule::generate);
    }

    @Override
    public Result<String, ApplicationError> compile(Map<Source, String> inputs) {
        Result<StringBuilder, ApplicationError> maybeCurrentOutput = new Ok<>(new StringBuilder());
        for (var entry : inputs.entrySet())
            maybeCurrentOutput = maybeCurrentOutput.flatMapValue(currentOutput -> this.compileSource(entry.getKey(),
                            entry.getValue())
                    .mapValue(currentOutput::append));

        return maybeCurrentOutput.mapValue(StringBuilder::toString);
    }
}