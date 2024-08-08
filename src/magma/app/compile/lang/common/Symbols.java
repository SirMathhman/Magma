package magma.app.compile.lang.common;

import magma.app.compile.lang.SymbolRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.TypeRule;

public class Symbols {
    public static final String SYMBOL = "symbol";
    public static final String VALUE = "value";

    public static TypeRule createSymbolRule() {
        return new TypeRule(SYMBOL, new StripRule(new SymbolRule(new StringRule(VALUE))));
    }
}
