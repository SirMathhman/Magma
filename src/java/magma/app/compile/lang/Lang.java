package magma.app.compile.lang;

import magma.app.compile.node.Node;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;

public class Lang {
    public static final String SEPARATOR = System.lineSeparator();

    private Lang() {
    }

    public static Rule<Node> createImportRule() {
        return new StripRule<>(new PrefixRule("import ",
                new SuffixRule(new LastRule(new StringRule("parent"), ".", new StringRule("destination")), ";")));
    }

    public static Rule<Node> createDependencyRule() {
        return new SuffixRule(new LastRule(new StringRule("source"), " --> ", new StringRule("destination")),
                Lang.SEPARATOR);
    }
}
