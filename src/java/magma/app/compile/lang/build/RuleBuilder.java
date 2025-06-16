package magma.app.compile.lang.build;

import magma.app.compile.error.DefaultCompileResultFactory;
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

public class RuleBuilder {
    public static Rule<NodeWithEverything> String(String value) {
        return new StringRule<>(value, new MapNodeFactory(), DefaultCompileResultFactory.createResultCompileResultFactory());
    }

    public static Rule<NodeWithEverything> Strip(Rule<NodeWithEverything> rule) {
        return new TruncateRule<>(rule, new StripTruncator(), DefaultCompileResultFactory.createResultCompileResultFactory());
    }

    public static Rule<NodeWithEverything> Last(Rule<NodeWithEverything> parent, String infix, Rule<NodeWithEverything> child) {
        return new LastRule<>(parent, infix, child, DefaultCompileResultFactory.createResultCompileResultFactory());
    }

    public static Rule<NodeWithEverything> Suffix(Rule<NodeWithEverything> last, String suffix) {
        return new TruncateRule<>(last, new SuffixTruncator(suffix), DefaultCompileResultFactory.createResultCompileResultFactory());
    }

    public static Rule<NodeWithEverything> Prefix(Rule<NodeWithEverything> suffix) {
        return new TruncateRule<>(suffix, new PrefixTruncator("import "), DefaultCompileResultFactory.createResultCompileResultFactory());
    }

    public static Rule<NodeWithEverything> NodeList(List<Rule<NodeWithEverything>> children) {
        return new NodeListRule<>("children", new OrRule<>(children, DefaultCompileResultFactory.createResultCompileResultFactory()), DefaultCompileResultFactory.createResultCompileResultFactory());
    }

    public static Rule<NodeWithEverything> Empty() {
        return new EmptyRule<>(new MapNodeFactory(), DefaultCompileResultFactory.createResultCompileResultFactory());
    }
}
