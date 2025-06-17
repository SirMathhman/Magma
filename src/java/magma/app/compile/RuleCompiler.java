package magma.app.compile;

import magma.api.result.Err;
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
import java.util.function.Function;

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
        Result<String, FormattedError> stringFormattedErrorResult1 = this.compileRoot(input, joinedName);
        Result<String, FormattedError> stringFormattedErrorResult = switch (stringFormattedErrorResult1) {
            case Err<String, FormattedError>(FormattedError error1) -> new Err<>(error1);
            case Ok<String, FormattedError>(String value1) ->
                    new Ok<>(((Function<String, String>) output -> "class " + joinedName + "\n" + output).apply(value1));
        };
        return switch (stringFormattedErrorResult) {
            case Err<String, FormattedError>(
                    FormattedError error
            ) -> new Err<>(((Function<FormattedError, ApplicationError>) ApplicationError::new).apply(error));
            case Ok<String, FormattedError>(String value) -> new Ok<>(value);
        };
    }

    Result<String, FormattedError> compileRoot(String input, String source) {
        Result<Node, FormattedError> nodeFormattedErrorResult = this.sourceRule.lex(input);
        return ((Result<Node, FormattedError>) switch (nodeFormattedErrorResult) {
            case Err<Node, FormattedError>(FormattedError error) -> new Err<>(error);
            case Ok<Node, FormattedError>(Node value) ->
                    new Ok<>(((Function<Node, Node>) children -> transform(source, children)).apply(value));
        })
                .flatMapValue(this.targetRule::generate);
    }

    @Override
    public Result<String, ApplicationError> compile(Map<Source, String> inputs) {
        Result<StringBuilder, ApplicationError> maybeCurrentOutput = new Ok<>(new StringBuilder());
        for (var entry : inputs.entrySet())
            maybeCurrentOutput = maybeCurrentOutput.flatMapValue(currentOutput -> {
                Result<String, ApplicationError> stringApplicationErrorResult = this.compileSource(entry.getKey(),
                        entry.getValue());
                return switch (stringApplicationErrorResult) {
                    case Err<String, ApplicationError>(ApplicationError error) -> new Err<>(error);
                    case Ok<String, ApplicationError>(
                            String value
                    ) -> new Ok<>(((Function<String, StringBuilder>) currentOutput::append).apply(value));
                };
            });

        return switch (maybeCurrentOutput) {
            case Err<StringBuilder, ApplicationError>(ApplicationError error) -> new Err<>(error);
            case Ok<StringBuilder, ApplicationError>(
                    StringBuilder value
            ) -> new Ok<>(((Function<StringBuilder, String>) StringBuilder::toString).apply(value));
        };
    }
}