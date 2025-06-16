package magma.app.compile.lang.build;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.TruncateRule;
import magma.app.compile.rule.divide.NodeListRule;
import magma.app.compile.rule.truncate.PrefixTruncator;
import magma.app.compile.rule.truncate.StripTruncator;
import magma.app.compile.rule.truncate.SuffixTruncator;

import java.util.List;

public class FactoryRuleBuilder<Error> implements RuleBuilder<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> {
    private final CompileResultFactory<NodeWithEverything, Error, StringResult<Error>, NodeResult<NodeWithEverything, Error>, NodeListResult<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>>> resultFactory;
    private final NodeFactory<NodeWithEverything> nodeFactory;

    public FactoryRuleBuilder(CompileResultFactory<NodeWithEverything, Error, StringResult<Error>, NodeResult<NodeWithEverything, Error>, NodeListResult<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>>> resultFactory, NodeFactory<NodeWithEverything> nodeFactory) {
        this.resultFactory = resultFactory;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> String(String value) {
        return new StringRule<>(value, this.nodeFactory, this.resultFactory);
    }

    @Override
    public Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> Strip(Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> rule) {
        return new TruncateRule<>(rule, new StripTruncator(), this.resultFactory);
    }

    @Override
    public Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> Last(Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> parent, String infix, Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> child) {
        return new LastRule<>(parent, infix, child, this.resultFactory);
    }

    @Override
    public Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> Suffix(Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> last, String suffix) {
        return new TruncateRule<>(last, new SuffixTruncator(suffix), this.resultFactory);
    }

    @Override
    public Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> Prefix(Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> suffix) {
        return new TruncateRule<>(suffix, new PrefixTruncator("import "), this.resultFactory);
    }

    @Override
    public Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> NodeList(List<Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>>> children) {
        return new NodeListRule<>("children", new OrRule<>(children, this.resultFactory), this.resultFactory);
    }

    @Override
    public Rule<NodeWithEverything, NodeResult<NodeWithEverything, Error>, StringResult<Error>> Empty() {
        return new EmptyRule<>(this.nodeFactory, this.resultFactory);
    }
}
