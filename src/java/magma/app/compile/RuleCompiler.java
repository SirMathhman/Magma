package magma.app.compile;

import magma.api.list.ListLikes;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.divide.Divide;
import magma.app.compile.error.FormattedError;
import magma.app.compile.lang.RuleFactory;
import magma.app.compile.node.property.CompoundNode;
import magma.app.compile.node.result.NodeErr;
import magma.app.compile.node.result.NodeOk;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.rule.Rule;
import magma.app.compile.string.StringErr;
import magma.app.compile.string.StringOk;
import magma.app.compile.string.StringResult;

import java.util.Map;

public class RuleCompiler implements Compiler {
    private final RuleFactory<Rule<CompoundNode, NodeResult<CompoundNode, FormattedError>, StringResult<FormattedError>>> sourceRuleFactory;
    private final RuleFactory<Rule<CompoundNode, NodeResult<CompoundNode, FormattedError>, StringResult<FormattedError>>> targetRuleFactory;

    public RuleCompiler(final RuleFactory<Rule<CompoundNode, NodeResult<CompoundNode, FormattedError>, StringResult<FormattedError>>> sourceRuleFactory, final RuleFactory<Rule<CompoundNode, NodeResult<CompoundNode, FormattedError>, StringResult<FormattedError>>> targetRuleFactory) {
        this.sourceRuleFactory = sourceRuleFactory;
        this.targetRuleFactory = targetRuleFactory;
    }

    private static boolean isFunctionalInterface(final String destination) {
        return ListLikes.of("Consumer", "Function", "Supplier")
                .contains(destination);
    }

    private StringResult<FormattedError> compileEntry(final Map<String, String> inputs) {
        StringResult<FormattedError> maybeCompiled = new StringOk<>();
        for (final var source : inputs.entrySet()) {
            final var name = source.getKey();
            final var input = source.getValue();

            maybeCompiled = maybeCompiled.tryAppendResult(() -> compile(input, name));
        }

        return maybeCompiled;
    }

    private StringResult<FormattedError> compile(final CharSequence input, final String name) {
        return Divide.divide(input)
                .stream()
                .<StringResult<FormattedError>>reduce(new StringOk<>(),
                        (output, segment) -> getStringResult(name, output, segment),
                        (_, next) -> next)
                .prepend("class " + name + System.lineSeparator());
    }

    private StringResult<FormattedError> getStringResult(final String name, final StringResult<FormattedError> output, final String segment) {
        final var tree = sourceRuleFactory.create()
                .lex(segment);

        return switch (getRecord(name, tree)) {
            case Err(final var error) -> new StringErr<>(error);
            case Ok(final var value) ->
                    value instanceof Some(final var result) ? output.tryAppendResult(() -> generate(result)) : output;
        };
    }

    private Result<Option<CompoundNode>, FormattedError> getRecord(final String name, final NodeResult<CompoundNode, FormattedError> tree) {
        return switch (tree) {
            case NodeErr(final var error1) -> new Err<>(error1);
            case NodeOk(final CompoundNode value1) -> new Ok<>(transform(name, value1));
        };
    }

    private Option<CompoundNode> transform(final String name, final CompoundNode destination) {
        final var node = destination.withString("source", name);
        final var destination1 = node.findString("destination")
                .orElse("");

        if (RuleCompiler.isFunctionalInterface(destination1))
            return new None<>();

        if (node.is("import"))
            return new Some<>(node.retype("dependency"));
        else
            return new None<>();
    }

    private StringResult<FormattedError> generate(final CompoundNode node) {
        return targetRuleFactory.create()
                .generate(node);
    }

    @Override
    public Result<String, FormattedError> compile(final Map<String, String> inputs) {
        return switch (compileEntry(inputs)) {
            case StringErr(final var error) -> new Err<>(error);
            case StringOk(final var value) -> new Ok<>(value);
        };
    }
}
