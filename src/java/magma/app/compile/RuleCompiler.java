package magma.app.compile;

import magma.api.Error;
import magma.api.collect.Joiner;
import magma.api.collect.iter.Iterable;
import magma.api.collect.list.ListCollector;
import magma.api.collect.list.Lists;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.error.ApplicationError;
import magma.app.io.Source;

import java.util.Map;

public class RuleCompiler implements Compiler {
    private final Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> targetRule;
    private final Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> sourceRule;

    public RuleCompiler(Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> sourceRule, Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> targetRule) {
        this.sourceRule = sourceRule;
        this.targetRule = targetRule;
    }

    static Node transform(String source, NodeWithNodeLists<Node> root) {
        final var transformed = root.findNodeList("children")
                .orElse(Lists.empty())
                .iter()
                .filter(node1 -> node1.is("import"))
                .map(node1 -> node1.withString("source", source))
                .collect(new ListCollector<>());

        final Node node = new MapNode();
        return node.withNodeList("children", transformed);
    }

    Result<String, Error> compileSource(Source source, String input) {
        final var namespace = source.computeNamespace();
        final var name = source.computeName();
        return this.getStringErrorResult(input, namespace, name);
    }

    private Result<String, Error> getStringErrorResult(String input, Iterable<String> namespace, String name) {
        final var joined = namespace.collect(new Joiner("."))
                .orElse("");

        final var joinedName = joined + "." + name;

        final var stringFormattedErrorResult1 = this.compileRoot(input, joinedName)
                .prependSlice("class " + joinedName + "\n");

        return this.toResult(stringFormattedErrorResult1)
                .mapErr(ApplicationError::new);
    }

    private Result<String, FormattedError> toResult(StringResult<FormattedError, Iterable<FormattedError>> result) {
        return switch (result) {
            case StringOk(var value) -> new Ok<>(value);
            case StringErr(FormattedError error) -> new Err<>(error);
        };
    }

    StringResult<FormattedError, Iterable<FormattedError>> compileRoot(String input, String source) {
        NodeResult<Node, FormattedError, Iterable<FormattedError>> nodeFormattedErrorResult = this.sourceRule.lex(input);
        NodeResult<Node, FormattedError, Iterable<FormattedError>> nodeFormattedErrorResult1 = nodeFormattedErrorResult.transform(
                value1 -> transform(source, value1));
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