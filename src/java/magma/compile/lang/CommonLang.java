package magma.compile.lang;

import jvm.collect.list.Lists;
import magma.compile.rule.LazyRule;
import magma.compile.rule.OptionalNodeListRule;
import magma.compile.rule.OptionalNodeRule;
import magma.compile.rule.Rule;
import magma.compile.rule.divide.CharDivider;
import magma.compile.rule.divide.DecoratedFolder;
import magma.compile.rule.divide.FoldingDivider;
import magma.compile.rule.divide.StatementFolder;
import magma.compile.rule.divide.ValueFolder;
import magma.compile.rule.locate.FirstLocator;
import magma.compile.rule.locate.LastLocator;
import magma.compile.rule.text.EmptyRule;
import magma.compile.rule.text.InfixRule;
import magma.compile.rule.text.PrefixRule;
import magma.compile.rule.text.StringRule;
import magma.compile.rule.text.StripRule;
import magma.compile.rule.text.SuffixRule;
import magma.compile.rule.tree.NodeListRule;
import magma.compile.rule.tree.NodeRule;
import magma.compile.rule.tree.OrRule;
import magma.compile.rule.tree.TypeRule;

public class CommonLang {
    static InfixRule createContentRule(Rule beforeContent, Rule childRule) {
        Rule children1 = createBlockRule(childRule);
        Rule right = new StripRule("", new SuffixRule(children1, "}"), "after-braces");
        return new InfixRule(beforeContent, "{", right, new FirstLocator());
    }

    static Rule createBlockRule(Rule childRule) {
        Rule children = new NodeListRule("children", new FoldingDivider(new DecoratedFolder(new StatementFolder())), childRule);
        return new NodeRule("content", new TypeRule("block", new StripRule("", children, "after-children")));
    }

    static Rule createSymbolRule(String propertyKey) {
        return new StripRule(new FilterRule(new SymbolFilter(), new StringRule(propertyKey)));
    }

    static Rule createDefinitionRule(Rule type) {
        NodeListRule modifiers = new NodeListRule("modifiers", new CharDivider(' '), new StringRule("modifier"));

        NodeRule typeProperty = new NodeRule("type", type);
        Rule beforeName = new OptionalNodeRule("modifiers",
                new InfixRule(modifiers, " ", typeProperty, new TypeSeparatorLocator()),
                typeProperty
        );

        return new StripRule(new InfixRule(beforeName, " ", createSymbolRule("name"), new LastLocator()));
    }

    static Rule createParamsRule(Rule definition) {
        return new OptionalNodeListRule("params", new NodeListRule("params", new FoldingDivider(new ValueFolder()), new OrRule(Lists.of(
                createWhitespaceRule(),
                definition
        ))), new EmptyRule());
    }

    static TypeRule createWhitespaceRule() {
        return new TypeRule("whitespace", new StripRule(new EmptyRule()));
    }

    static TypeRule createAssignmentRule() {
        return new TypeRule("assignment", new SuffixRule(new InfixRule(new StringRule("destination"), "=", new StringRule("source"), new FirstLocator()), ";"));
    }

    static TypeRule createElseRule() {
        return new TypeRule("else", new StripRule(new PrefixRule("else", new StringRule("content"))));
    }

    static TypeRule createPostfixRule() {
        return new TypeRule("postfix", new SuffixRule(new StringRule("value"), "++;"));
    }

    static TypeRule createForRule() {
        return new TypeRule("for", new StripRule(new PrefixRule("for ", new StringRule("content"))));
    }

    static TypeRule createIfRule() {
        return new TypeRule("if", new StripRule(new PrefixRule("if", new StringRule("content"))));
    }

    static TypeRule createReturnRule(Rule value) {
        PrefixRule rule = new PrefixRule("return ", new SuffixRule(new NodeRule("value", value), ";"));
        return new TypeRule("return", new StripRule(rule));
    }

    static TypeRule createWhileRule() {
        return new TypeRule("while", new StripRule(new PrefixRule("while", new StringRule("content"))));
    }

    public static Rule createGenericRule(Rule type) {
        Rule typeArguments = new NodeListRule("arguments", new FoldingDivider(new ValueFolder()), new OrRule(Lists.of(
                createWhitespaceRule(),
                type
        )));

        Rule base = new NodeRule("base", createQualifiedRule());
        return new TypeRule("generic", new StripRule(new SuffixRule(new InfixRule(base, "<", typeArguments, new FirstLocator()), ">")));
    }

    static Rule createQualifiedRule() {
        return new TypeRule("qualified", new NodeListRule("segments", new CharDivider('.'), createSymbolRule("value")));
    }

    static Rule createNamedWithTypeParams() {
        Rule name = createSymbolRule("name");
        Rule typeParams = new NodeListRule("type-params", new FoldingDivider(new ValueFolder()), createSymbolRule("value"));

        return new OptionalNodeListRule("type-params",
                new StripRule(new InfixRule(name, "<", new SuffixRule(typeParams, ">"), new FirstLocator())),
                name
        );
    }

    static TypeRule createSymbolValueRule() {
        return new TypeRule("symbol-value", createSymbolRule("value"));
    }

    static TypeRule createOperatorRule(LazyRule value, String type, String infix) {
        NodeRule left = new NodeRule("left", value);
        NodeRule right = new NodeRule("right", value);
        return new TypeRule(type, new InfixRule(left, infix, right, new FirstLocator()));
    }

    static TypeRule createInvocationRule(Rule value) {
        return new TypeRule("invocation", new StripRule(new SuffixRule(new InfixRule(new NodeRule("caller", value), "(", createArgumentsRule(value), new InvocationStartLocator()), ")")));
    }

    static NodeListRule createArgumentsRule(Rule value) {
        return new NodeListRule("arguments", new FoldingDivider(new DecoratedFolder(new ValueFolder())), value);
    }

    static TypeRule createAccessRule(String type, String infix, Rule value) {
        return createDataAccess(type, infix, value, createSymbolRule("property"));
    }

    static TypeRule createDataAccess(String type, String infix, Rule value, Rule property) {
        return new TypeRule(type, new InfixRule(new NodeRule("child", value), infix, property, new LastLocator()));
    }

    static TypeRule createStringRule() {
        return new TypeRule("string", new StripRule(new PrefixRule("\"", new SuffixRule(new StringRule("value"), "\""))));
    }

    static TypeRule createTernaryRule(LazyRule value) {
        NodeRule condition = new NodeRule("condition", value);
        NodeRule whenTrue = new NodeRule("when-true", value);
        NodeRule whenFalse = new NodeRule("when-false", value);
        return new TypeRule("ternary", new InfixRule(condition, "?", new InfixRule(whenTrue, ":", whenFalse, new FirstLocator()), new FirstLocator()));
    }

    static TypeRule createNumberRule() {
        return new TypeRule("number", new StripRule(new FilterRule(new NumberFilter(), new StringRule("value"))));
    }

    static TypeRule createNotRule(LazyRule value) {
        return new TypeRule("not", new PrefixRule("!", new NodeRule("child", value)));
    }

    static TypeRule createCharRule() {
        return new TypeRule("char", new PrefixRule("'", new SuffixRule(new StringRule("value"), "'")));
    }
}