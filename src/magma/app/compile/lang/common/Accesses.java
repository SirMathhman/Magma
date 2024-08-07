package magma.app.compile.lang.common;

import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.Last;
import magma.app.compile.rule.locate.LocateRule;

public class Accesses {
    public static TypeRule createAccessRule(LazyRule value) {
        return new TypeRule("access", new LocateRule(new NodeRule("object", value), new Last("."), new StringRule("member")));
    }
}
