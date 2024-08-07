package magma.app.compile.lang.java;

import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.CommonLang;
import magma.app.compile.lang.common.Declarations;
import magma.app.compile.lang.common.PrefixedStatements;
import magma.app.compile.lang.common.Symbols;
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
    public static final String CLASS_TYPE = "class";
    public static final String PACKAGE = "package";
    public static final String CLASS_NAME = "name";
    public static final String METHOD_TYPE = "method";
    public static final String METHOD_DEFINITION = "definition";
    public static final String ARRAY = "array";
    public static final String CONSTRUCTION = "construction";

    public static Rule createRootJavaRule() {
        var childRule = new DisjunctionRule(List.of(
                CommonLang.createNamespaceRule("package", "package "),
                CommonLang.createImportRule(),
                createClassRule()
        ));

        return new DisjunctionRule(List.of(
                Blocks.createBlockRule(childRule),
                EmptyRule.EMPTY_RULE
        ));
    }

    private static Rule createClassRule() {
        var classRule = new LazyRule();
        var modifiers = CommonLang.createModifiersRule();

        var definition = createDefinitionRule();
        var classMember = new DisjunctionRule(List.of(
                createMethodRule(definition),
                CommonLang.createDefinitionStatement(definition),
                classRule
        ));

        var content = new NodeRule("value", Blocks.createBlockRule(classMember));
        var after = new LocateRule(new StripRule(new StringRule(CLASS_NAME)), new First("{"), new SuffixRule(content, "}"));
        classRule.set(new TypeRule("class", new LocateRule(modifiers, new First("class "), after)));
        return classRule;
    }

    private static TypeRule createMethodRule(TypeRule definition) {
        var params = new DisjunctionRule(List.of(
                CommonLang.createParamsRule(definition),
                EmptyRule.EMPTY_RULE
        ));

        var beforeContent = new LocateRule(new NodeRule(METHOD_DEFINITION, definition), new First("("), new StripRule(new SuffixRule(params, ")")));
        var statements = createStatementRule(definition, createValueRule());
        var children = new NodeRule("value", Blocks.createBlockRule(statements));
        var content = new SuffixRule(children, "}");
        return new TypeRule(METHOD_TYPE, new LocateRule(beforeContent, new First("{"), content));
    }

    private static Rule createStatementRule(Rule definition, Rule value) {
        var statement = new LazyRule();
        statement.set(new DisjunctionRule(List.of(
                PrefixedStatements.createTryRule(statement),
                CommonLang.createCatchRule(definition, statement),
                Declarations.createDeclarationRule(definition, value),
                new TypeRule(CONSTRUCTION, new SuffixRule(createConstructionRule(value), ";")),
                CommonLang.createInvocationStatementRule(value),
                CommonLang.createCommentRule(),
                CommonLang.createReturnRule(value),
                CommonLang.createAssignmentRule(value),
                CommonLang.createConditionRule("if", "if", value, statement),
                CommonLang.createConditionRule("while", "while", value, statement),
                CommonLang.createDefinitionStatement(definition),
                createElseRule(statement)
        )));

        return statement;
    }

    private static TypeRule createElseRule(LazyRule statement) {
        var value = new NodeRule("value", Blocks.createBlockRule(statement));
        return new TypeRule("else", new PrefixRule("else", new StripRule(new PrefixRule("{", new SuffixRule(value, "}")))));
    }

    private static Rule createValueRule() {
        var value = new LazyRule();
        value.set(new DisjunctionRule(List.of(
                createConstructionRule(value),
                createInvocationRule(value),
                CommonLang.createAccessRule(value),
                CommonLang.createReferenceRule(),
                CommonLang.createStringRule(),
                createOperatorRule("and", "&&", value),
                createOperatorRule("equals", "==", value),
                new TypeRule("char", new StripRule(new PrefixRule("'", new SuffixRule(new StringRule("value"), "'")))),
                CommonLang.createNumberRule()
        )));
        return value;
    }

    private static TypeRule createOperatorRule(String type, String operator, LazyRule value) {
        return new TypeRule(type, new LocateRule(new NodeRule("left", value), new First(operator), new NodeRule("right", value)));
    }

    private static TypeRule createInvocationRule(LazyRule value) {
        return CommonLang.createOperationsRule(CommonLang.INVOCATION, new NodeRule("caller", value), value);
    }

    private static Rule createConstructionRule(Rule value) {
        var callerProperty = new NodeRule("caller", value);
        var caller = new StripRule(new PrefixRule("new", new StripRule(new DisjunctionRule(List.of(
                new SuffixRule(callerProperty, "<>"),
                callerProperty
        )))));
        return CommonLang.createOperationsRule(CONSTRUCTION, caller, value);
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
                new TypeRule(ARRAY, new SuffixRule(new NodeRule("child", rule), "[]")),
                Symbols.createSymbolRule()
        )));
        return rule;
    }

}
