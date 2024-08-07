package magma.app.compile.lang.common;

import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.TypeRule;

public class Comments {
    public static TypeRule createCommentRule() {
        return new TypeRule("comment", new PrefixRule("//", new StringRule("value")));
    }
}
