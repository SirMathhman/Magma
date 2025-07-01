package magma.rule;

import magma.compile.result.ResultFactory;
import magma.error.FormatError;
import magma.node.NodeWithStrings;
import magma.node.factory.NodeFactory;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public final class StringRule<NOde extends NodeWithStrings<NOde>>
        implements Rule<NOde, NodeResult<NOde>, StringResult<FormatError>> {
    private final String key;
    private final ResultFactory<NOde, StringResult<FormatError>> resultFactory;
    private final NodeFactory<NOde> nodeFactory;

    public StringRule(final String key,
                      final ResultFactory<NOde, StringResult<FormatError>> resultFactory,
                      final NodeFactory<NOde> nodeFactory) {
        this.key = key;
        this.resultFactory = resultFactory;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public NodeResult<NOde> lex(final String input) {
        final var node = this.nodeFactory.createNode().withString(this.key, input);
        return this.resultFactory.createNode(node);
    }

    @Override
    public StringResult<FormatError> generate(final NOde node) {
        return node.findString(this.key)
                   .map(this.resultFactory::createString)
                   .orElseGet(
                           () -> this.resultFactory.createStringError("String '" + this.key + "' not present", node));
    }
}