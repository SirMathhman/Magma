package magma.app.compile.lang;

import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.First;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
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
    public static final String CLASS_TYPE = "class";
    public static final String PACKAGE = "package";
    public static final String CLASS_NAME = "name";
    public static final String METHOD_TYPE = "method";
    public static final String METHOD_DEFINITION = "definition";

    public static Rule createRootJavaRule() {
        var childRule = new DisjunctionRule(List.of(
                CommonLang.createNamespaceRule("package", "package "),
                CommonLang.createImportRule(),
                createClassRule()
        ));

        return CommonLang.createBlockRule(childRule);
    }

    private static TypeRule createClassRule() {
        var modifiers = CommonLang.createModifiersRule();

        var classMember = new DisjunctionRule(List.of(
                createMethodRule()
        ));

        var content = new NodeRule("value", CommonLang.createBlockRule(classMember));
        var after = new LocateRule(new StripRule(new StringRule(CLASS_NAME)), new First("{"), new SuffixRule(content, "}"));
        return new TypeRule("class", new LocateRule(modifiers, new First("class "), after));
    }

    private static TypeRule createMethodRule() {
        var definition = createDefinitionRule();
        var params = new DisjunctionRule(List.of(
                CommonLang.createParamsRule(definition),
                EmptyRule.EMPTY_RULE
        ));

        var beforeContent = new LocateRule(new NodeRule(METHOD_DEFINITION, definition), new First("("), new StripRule(new SuffixRule(params, ")")));
        var statements = createStatementRule(definition, createValueRule());
        var children = new NodeRule("value", CommonLang.createBlockRule(statements));
        var content = new SuffixRule(children, "}");
        return new TypeRule(METHOD_TYPE, new LocateRule(beforeContent, new First("{"), content));
    }

    private static Rule createStatementRule(Rule definition, Rule value) {
        var statement = new LazyRule();
        statement.set(new DisjunctionRule(List.of(
                CommonLang.createTryRule(statement),
                CommonLang.createCatchRule(definition, statement),
                CommonLang.createDeclarationRule(definition, value),
                new TypeRule("construction", new SuffixRule(createConstructionRule(value), ";")),
                CommonLang.createInvocationStatementRule(value),
                CommonLang.createCommentRule(),
                CommonLang.createReturnRule(value))
        ));
        return statement;
    }

    private static Rule createValueRule() {
        var value = new LazyRule();
        value.set(new DisjunctionRule(List.of(
                createConstructionRule(value),
                CommonLang.createInvocationRule(value),
                CommonLang.createAccessRule(value),
                CommonLang.createReferenceRule(),
                new TypeRule("string", new StripRule(new PrefixRule("\"", new SuffixRule(new StringRule("value"), "\""))))
        )));
        return value;
    }

    private static Rule createConstructionRule(Rule value) {
        return CommonLang.createOperationsRule(value, new StripRule(new PrefixRule("new", new StripRule(new NodeRule("caller", value)))));
    }

    private static TypeRule createDefinitionRule() {
        var modifiers = CommonLang.createModifiersRule();
        var type = new NodeRule("type", createTypeRule());

        var modifiersAndType = new DisjunctionRule(List.of(
                new LocateRule(modifiers, new Last(" "), type),
                type
        ));

        var name = new StringRule(CLASS_NAME);
        return new TypeRule(METHOD_DEFINITION, new StripRule(new LocateRule(modifiersAndType, new Last(" "), name)));
    }

    private static Rule createTypeRule() {
        var rule = new LazyRule();
        rule.set(new DisjunctionRule(List.of(
                new TypeRule("array", new SuffixRule(new NodeRule("child", rule), "[]")),
                new TypeRule("symbol", new StringRule("value"))
        )));
        return rule;
    }

}
