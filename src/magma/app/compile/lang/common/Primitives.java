package magma.app.compile.lang.common;

import magma.app.compile.lang.DigitRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public class Primitives {
    public static TypeRule createStringRule() {
        return new TypeRule("string", new StripRule(new PrefixRule("\"", new SuffixRule(new StringRule("value"), "\""))));
    }

    public static TypeRule createNumberRule() {
        return new TypeRule("number", new StripRule(new DigitRule(new StringRule("value"))));
    }

    public static TypeRule createCharRule() {
        return new TypeRule("char", new StripRule(new PrefixRule("'", new SuffixRule(new StringRule("value"), "'"))));
    }

    public static TypeRule createPostRule(String type, String suffix, Rule value) {
        return new TypeRule("post-" + type, new StripRule(new SuffixRule(new NodeRule("child", value), suffix + ";")));
    }

    public static TypeRule createNotRule(LazyRule value) {
        return new TypeRule("not", new PrefixRule("!", new NodeRule("value", value)));
    }
}
