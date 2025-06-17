package magma.app.compile;

import magma.api.Error;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.NodeErr;
import magma.app.compile.error.NodeOk;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringErr;
import magma.app.compile.error.StringOk;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.Rule;
import magma.app.error.ApplicationError;
import magma.app.io.Source;

import java.util.ArrayList;
import java.util.Map;

public class RuleCompiler implements Compiler {
    private final Rule<Node, NodeResult, StringResult> targetRule;
    private final Rule<Node, NodeResult, StringResult> sourceRule;

    public RuleCompiler(Rule<Node, NodeResult, StringResult> sourceRule, Rule<Node, NodeResult, StringResult> targetRule) {
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
        NodeResult nodeFormattedErrorResult = this.sourceRule.lex(input);
        NodeResult nodeFormattedErrorResult1 = switch (nodeFormattedErrorResult) {
            case NodeErr(FormattedError error) -> new NodeErr(error);
            case NodeOk(Node value) -> new NodeOk(transform(source, value));
        };
        return switch (nodeFormattedErrorResult1) {
            case NodeErr(FormattedError error1) -> new StringErr(error1);
            case NodeOk(
                    Node value1
            ) -> this.targetRule.generate(value1);
        };
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