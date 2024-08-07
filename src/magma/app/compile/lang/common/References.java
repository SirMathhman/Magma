package magma.app.compile.lang.common;

import magma.app.compile.lang.SymbolRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.TypeRule;

public class References {
    public static TypeRule createReferenceRule() {
        return new TypeRule("reference", new StripRule(new SymbolRule(new StringRule("value"))));
    }
}
