package magma.app.compile.lang;

import magma.api.error.list.ErrorSequence;
import magma.api.list.ListLike;
import magma.app.compile.accumulate.ImmutableAccumulatorFactory;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.StringNode;
import magma.app.compile.node.TypedNode;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.string.StringResult;

class Lang<Node extends DisplayNode & StringNode<Node> & TypedNode<Node>, Error> {
    private final ResultFactory<Node, NodeResult<Node, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory;
    private final NodeFactory<Node> nodeFactory;

    Lang(final ResultFactory<Node, NodeResult<Node, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory, final NodeFactory<Node> nodeFactory) {
        this.resultFactory = resultFactory;
        this.nodeFactory = nodeFactory;
    }

    Rule<Node, NodeResult<Node, Error>, StringResult<Error>> Or(final ListLike<Rule<Node, NodeResult<Node, Error>, StringResult<Error>>> rules) {
        return new OrRule<>(rules, resultFactory, new ImmutableAccumulatorFactory<>());
    }

    Rule<Node, NodeResult<Node, Error>, StringResult<Error>> String(final String key) {
        return new StringRule<>(key, nodeFactory, resultFactory);
    }

    Rule<Node, NodeResult<Node, Error>, StringResult<Error>> Suffix(final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule, final String suffix) {
        return new SuffixRule<>(rule, suffix, resultFactory);
    }

    Rule<Node, NodeResult<Node, Error>, StringResult<Error>> Type(final String type, final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule) {
        return new TypeRule<>(type, rule, resultFactory);
    }

    Rule<Node, NodeResult<Node, Error>, StringResult<Error>> Infix(final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rightRule, final String infix, final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> leftRule) {
        return new InfixRule<>(leftRule, infix, rightRule, resultFactory);
    }

    Rule<Node, NodeResult<Node, Error>, StringResult<Error>> Prefix(final String type, final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule) {
        return new PrefixRule<>(type + " ", rule, resultFactory);
    }

    Rule<Node, NodeResult<Node, Error>, StringResult<Error>> Strip(final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule) {
        return new StripRule<>(rule);
    }
}
