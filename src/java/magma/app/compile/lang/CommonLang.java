package magma.app.compile.lang;

import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.divide.DivideRule;

import java.util.List;

public class CommonLang {
    static Rule<NodeWithEverything> Divide(List<Rule<NodeWithEverything>> rules) {
        return new DivideRule<>("children", new OrRule<NodeWithEverything>(rules), new MapNodeFactory());
    }
}
