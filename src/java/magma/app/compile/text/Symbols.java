package magma.app.compile.text;

import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SymbolRule;
import magma.app.compile.rule.TypeRule;

public class Symbols {
    public static StripRule createSymbolRule() {
        return new StripRule(new SymbolRule(new TypeRule("symbol", new StringRule("value"))));
    }
}
