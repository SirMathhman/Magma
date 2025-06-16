package magma.app.compile.lang.build;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.MapNodeFactory;
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

public class FactoryRuleBuilder implements RuleBuilder {
    private final CompileResultFactory<NodeWithEverything, StringResult, NodeResult<NodeWithEverything>, NodeListResult<NodeWithEverything>> factory;

    public FactoryRuleBuilder(CompileResultFactory<NodeWithEverything, StringResult, NodeResult<NodeWithEverything>, NodeListResult<NodeWithEverything>> factory) {
        this.factory = factory;
    }

    @Override
    public Rule<NodeWithEverything> String(String value) {
        return new StringRule<>(value, new MapNodeFactory(), this.factory);
    }

    @Override
    public Rule<NodeWithEverything> Strip(Rule<NodeWithEverything> rule) {
        return new TruncateRule<>(rule, new StripTruncator(), this.factory);
    }

    @Override
    public Rule<NodeWithEverything> Last(Rule<NodeWithEverything> parent, String infix, Rule<NodeWithEverything> child) {
        return new LastRule<>(parent, infix, child, this.factory);
    }

    @Override
    public Rule<NodeWithEverything> Suffix(Rule<NodeWithEverything> last, String suffix) {
        return new TruncateRule<>(last, new SuffixTruncator(suffix), this.factory);
    }

    @Override
    public Rule<NodeWithEverything> Prefix(Rule<NodeWithEverything> suffix) {
        return new TruncateRule<>(suffix, new PrefixTruncator("import "), this.factory);
    }

    @Override
    public Rule<NodeWithEverything> NodeList(List<Rule<NodeWithEverything>> children) {
        return new NodeListRule<>("children", new OrRule<>(children, this.factory), this.factory);
    }

    @Override
    public Rule<NodeWithEverything> Empty() {
        return new EmptyRule<>(new MapNodeFactory(), this.factory);
    }
}
