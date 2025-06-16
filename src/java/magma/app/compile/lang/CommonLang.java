package magma.app.compile.lang;

import magma.app.compile.lang.everything.EverythingRule;
import magma.app.compile.lang.everything.EverythingRuleImpl;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.divide.DivideRule;

import java.util.List;

public class CommonLang {
    static EverythingRule Divide(List<EverythingRule> rules) {
        return new EverythingRuleImpl(new DivideRule<>("children", new OrRule<>(rules), new MapNodeFactory()));
    }
}
