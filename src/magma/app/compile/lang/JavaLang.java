package magma.app.compile.lang;

import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.FirstRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StatementsRule;
import magma.app.compile.rule.StringListRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class JavaLang {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String VOID_KEYWORD_WITH_SPACE = "void ";
    public static final String METHOD_SUFFIX = "(){}";
    public static final String METHOD = "method";
    public static final String CLASS = "class";
    public static final String PACKAGE = "package";

    public static Rule createRootJavaRule() {
        var childRule = new DisjunctionRule(List.of(
                createNamespaceRule("package", "package "),
                createNamespaceRule("import", "import "),
                new TypeRule("class", new FirstRule(new StripRule(new StringRule("modifiers")), "class ", new StringRule("after"))),
                new TypeRule("any", new StringRule("value"))
        ));

        return new TypeRule("block", new StatementsRule("children", childRule));
    }

    private static Rule createNamespaceRule(String type, String prefix) {
        return new TypeRule(type, new PrefixRule(prefix, new SuffixRule(new StringListRule("namespace", "."), ";")));
    }
}
