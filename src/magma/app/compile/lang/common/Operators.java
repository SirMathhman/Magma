package magma.app.compile.lang.common;

import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.LocateRule;

public class Operators {
    public static TypeRule createOperatorRule(String type, String operator, LazyRule value) {
        return new TypeRule(type, new LocateRule(new NodeRule("left", value), new First(operator), new NodeRule("right", value)));
    }
}
