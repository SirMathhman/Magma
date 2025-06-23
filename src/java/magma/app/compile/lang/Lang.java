package magma.app.compile.lang;

import magma.api.error.list.ErrorSequence;
import magma.api.list.Streamable;
import magma.app.compile.accumulate.ImmutableAccumulatorFactory;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.property.CompoundNode;
import magma.app.compile.node.property.NodeFactory;
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

class Lang<Error> {
    private final ResultFactory<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory;
    private final NodeFactory<CompoundNode> nodeFactory;

    Lang(final ResultFactory<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory, final NodeFactory<CompoundNode> nodeFactory) {
        this.resultFactory = resultFactory;
        this.nodeFactory = nodeFactory;
    }

    Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> Or(final Streamable<Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>>> rules) {
        return new OrRule<>(rules, resultFactory, new ImmutableAccumulatorFactory<>());
    }

    Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> String(final String key) {
        return new StringRule<>(key, nodeFactory, resultFactory);
    }

    Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> Suffix(final Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> rule, final String suffix) {
        return new SuffixRule<>(rule, suffix, resultFactory);
    }

    Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> Type(final String type, final Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> rule) {
        return new TypeRule<>(type, rule, resultFactory);
    }

    Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> Infix(final Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> rightRule, final String infix, final Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> leftRule) {
        return new InfixRule<>(leftRule, infix, rightRule, resultFactory);
    }

    Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> Prefix(final String type, final Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> rule) {
        return new PrefixRule<>(type + " ", rule, resultFactory);
    }

    Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> Strip(final Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> rule) {
        return new StripRule<>(rule);
    }
}
