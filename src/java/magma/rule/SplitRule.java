package magma.rule;

import magma.api.Tuple;
import magma.error.CompileError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.rule.locate.FirstLocator;
import magma.rule.locate.LastLocator;
import magma.rule.split.InfixSplitter;
import magma.rule.split.Splitter;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.Optional;

public final class SplitRule implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> leftRule;
    private final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rightRule;
    private final Splitter splitter;

    private SplitRule(final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> leftRule, final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rightRule, final Splitter splitter) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.splitter = splitter;
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> Last(final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> leftRule, final String infix, final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new LastLocator()));
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> First(final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> leftRule, final String infix, final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new FirstLocator()));
    }

    private Optional<EverythingNode> lex0(final String input) {
        return this.splitter.split(input).flatMap(this::lexWithTuple);
    }

    private Optional<String> generate0(final EverythingNode node) {
        return this.leftRule.generate(node).toOptional().flatMap(leftResult -> {
            final var generated = this.rightRule.generate(node).toOptional();
            return generated.map(rightResult -> this.splitter.join(leftResult, rightResult));
        });
    }

    private Optional<EverythingNode> lexWithTuple(final Tuple<String, String> tuple) {
        final var leftSlice = tuple.left();
        final var rightSlice = tuple.right();

        return this.leftRule.lex(leftSlice).toOptional().flatMap(leftResult -> {
            final var rightResult = this.rightRule.lex(rightSlice).toOptional();
            return rightResult.map(leftResult::merge);
        });
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(
                () -> new NodeErr<EverythingNode>(new CompileError()));
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.generate0(node).<StringResult>map(StringOk::new).orElseGet(() -> new StringErr(new CompileError()));
    }
}