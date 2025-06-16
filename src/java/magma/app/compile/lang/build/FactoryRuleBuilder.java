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

public class FactoryRuleBuilder<Error> implements RuleBuilder<Error> {
    private final CompileResultFactory<NodeWithEverything, Error, StringResult<Error>, NodeResult<NodeWithEverything, Error>, NodeListResult<NodeWithEverything, Error>> factory;

    public FactoryRuleBuilder(CompileResultFactory<NodeWithEverything, Error, StringResult<Error>, NodeResult<NodeWithEverything, Error>, NodeListResult<NodeWithEverything, Error>> factory) {
        this.factory = factory;
    }

    @Override
    public Rule<NodeWithEverything, Error> String(String value) {
        return new StringRule<>(value, new MapNodeFactory(), this.factory);
    }

    @Override
    public Rule<NodeWithEverything, Error> Strip(Rule<NodeWithEverything, Error> rule) {
        return new TruncateRule<>(rule, new StripTruncator(), this.factory);
    }

    @Override
    public Rule<NodeWithEverything, Error> Last(Rule<NodeWithEverything, Error> parent, String infix, Rule<NodeWithEverything, Error> child) {
        return new LastRule<>(parent, infix, child, this.factory);
    }

    @Override
    public Rule<NodeWithEverything, Error> Suffix(Rule<NodeWithEverything, Error> last, String suffix) {
        return new TruncateRule<>(last, new SuffixTruncator(suffix), this.factory);
    }

    @Override
    public Rule<NodeWithEverything, Error> Prefix(Rule<NodeWithEverything, Error> suffix) {
        return new TruncateRule<>(suffix, new PrefixTruncator("import "), this.factory);
    }

    @Override
    public Rule<NodeWithEverything, Error> NodeList(List<Rule<NodeWithEverything, Error>> children) {
        return new NodeListRule<>("children", new OrRule<>(children, this.factory), this.factory);
    }

    @Override
    public Rule<NodeWithEverything, Error> Empty() {
        return new EmptyRule<>(new MapNodeFactory(), this.factory);
    }
}
