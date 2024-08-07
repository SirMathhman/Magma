package magma.app.compile.lang.common;

import magma.app.compile.rule.StringListRule;
import magma.app.compile.rule.StripRule;

public class Modifiers {
    public static final String MODIFIERS = "modifiers";

    public static StripRule createModifiersRule() {
        return new StripRule(new StringListRule(MODIFIERS, " "));
    }
}
