package magma.app.compile.lang;

import magma.app.compile.MemberSplitter;
import magma.app.compile.ParamSplitter;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.First;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
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
                createClassRule()
        ));

        return new TypeRule("block", new NodeListRule(new MemberSplitter(), "children", childRule));
    }

    private static TypeRule createClassRule() {
        var modifiers = new StripRule(new StringListRule("modifiers", " "));

        var classMember = new DisjunctionRule(List.of(
                createMethodRule()
        ));

        var content = new NodeListRule(new MemberSplitter(), "children", classMember);

        var after = new LocateRule(new StripRule(new StringRule("name")), new First("{"), new SuffixRule(content, "}"));
        return new TypeRule("class", new LocateRule(modifiers, new First("class "), after));
    }

    private static TypeRule createMethodRule() {
        var definition = createDefinitionRule();
        var params = new StripRule(new SuffixRule(new NodeListRule(new ParamSplitter(), "params", definition), ")"));

        var beforeContent = new LocateRule(new NodeRule("definition", definition), new First("("), params);
        return new TypeRule("method", new LocateRule(beforeContent, new First("{"), new StringRule("content")));
    }

    private static TypeRule createDefinitionRule() {
        var modifiers = new StripRule(new StringListRule("modifiers", " "));
        var type = new NodeRule("type", createTypeRule());

        var modifiersAndType = new DisjunctionRule(List.of(
                new LocateRule(modifiers, new Last(" "), type),
                type
        ));

        var name = new StringRule("name");

        return new TypeRule("definition", new LocateRule(modifiersAndType, new Last(" "), name));
    }

    private static Rule createTypeRule() {
        var rule = new LazyRule();
        rule.set(new DisjunctionRule(List.of(
                new TypeRule("array", new SuffixRule(new NodeRule("child", rule), "[]")),
                new TypeRule("symbol", new StringRule("value"))
        )));
        return rule;
    }

    private static Rule createNamespaceRule(String type, String prefix) {
        return new TypeRule(type, new PrefixRule(prefix, new SuffixRule(new StringListRule("namespace", "."), ";")));
    }
}
