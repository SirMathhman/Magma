package magma.app.compile;

import magma.api.Error;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.rule.Rule;
import magma.app.error.ApplicationError;
import magma.app.io.Source;

import java.util.ArrayList;
import java.util.Map;

public class RuleCompiler implements Compiler {
    private final Rule<Node, NodeResult<Node, FormattedError>, StringResult> targetRule;
    private final Rule<Node, NodeResult<Node, FormattedError>, StringResult> sourceRule;

    public RuleCompiler(Rule<Node, NodeResult<Node, FormattedError>, StringResult> sourceRule, Rule<Node, NodeResult<Node, FormattedError>, StringResult> targetRule) {
        this.sourceRule = sourceRule;
        this.targetRule = targetRule;
    }

    static Node transform(String source, NodeWithNodeLists<Node> root) {
        final var transformed = root.findNodeList("children")
                .orElse(new ArrayList<>())
                .stream()
                .filter(node -> node.is("import"))
                .map(node -> node.withString("source", source))
                .toList();

        final Node node = new MapNode();
        return node.withNodeList("children", transformed);
    }

    Result<String, Error> compileSource(Source source, String input) {
        final var namespace = source.computeNamespace();
        final var name = source.computeName();

        final var joined = String.join(".", namespace);
        final var joinedName = joined + "." + name;
        StringResult stringFormattedErrorResult1 = this.compileRoot(input, joinedName);
        StringResult stringFormattedErrorResult = switch (stringFormattedErrorResult1) {
            case StringErr(FormattedError error1) -> new StringErr(error1);
            case StringOk(String value1) -> new StringOk("class " + joinedName + "\n" + value1);
        };
        return switch (stringFormattedErrorResult) {
            case StringErr(FormattedError error) -> new Err<>(new ApplicationError(error));
            case StringOk(String value) -> new Ok<>(value);
        };
    }

    StringResult compileRoot(String input, String source) {
        NodeResult<Node, FormattedError> nodeFormattedErrorResult = this.sourceRule.lex(input);
        NodeResult<Node, FormattedError> nodeFormattedErrorResult1 = nodeFormattedErrorResult.transform(value1 -> transform(
                source,
                value1));
        return nodeFormattedErrorResult1.generate(this.targetRule::generate);
    }

    @Override
    public Result<String, Error> compile(Map<Source, String> inputs) {
        Result<StringBuilder, Error> maybeCurrentOutput = new Ok<>(new StringBuilder());
        for (var entry : inputs.entrySet())
            maybeCurrentOutput = this.getMaybeCurrentOutput(entry, maybeCurrentOutput);

        return this.getStringApplicationErrorResult(maybeCurrentOutput);
    }

    private Result<StringBuilder, Error> getMaybeCurrentOutput(Map.Entry<Source, String> entry, Result<StringBuilder, Error> maybeCurrentOutput) {
        return switch (maybeCurrentOutput) {
            case Err<StringBuilder, Error>(Error error1) -> new Err<>(error1);
            case Ok<StringBuilder, Error>(StringBuilder value1) -> {
                Result<String, Error> stringApplicationErrorResult = this.compileSource(entry.getKey(),
                        entry.getValue());
                yield this.getStringBuilderApplicationErrorResult(value1, stringApplicationErrorResult);
            }
        };
    }

    private Result<String, Error> getStringApplicationErrorResult(Result<StringBuilder, Error> maybeCurrentOutput) {
        return switch (maybeCurrentOutput) {
            case Err<StringBuilder, Error>(Error error) -> new Err<>(error);
            case Ok<StringBuilder, Error>(
                    StringBuilder value
            ) -> new Ok<>(value.toString());
        };
    }

    private Result<StringBuilder, Error> getStringBuilderApplicationErrorResult(StringBuilder value1, Result<String, Error> stringApplicationErrorResult) {
        return switch (stringApplicationErrorResult) {
            case Err<String, Error>(Error error) -> new Err<>(error);
            case Ok<String, Error>(
                    String value
            ) -> new Ok<>(value1.append(value));
        };
    }
}