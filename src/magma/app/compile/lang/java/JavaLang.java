package magma.app.compile.lang.java;

import magma.app.compile.lang.common.Accesses;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.Comments;
import magma.app.compile.lang.common.Conditions;
import magma.app.compile.lang.common.Declarations;
import magma.app.compile.lang.common.Definitions;
import magma.app.compile.lang.common.Functions;
import magma.app.compile.lang.common.Modifiers;
import magma.app.compile.lang.common.Namespace;
import magma.app.compile.lang.common.Operations;
import magma.app.compile.lang.common.Operators;
import magma.app.compile.lang.common.PrefixedStatements;
import magma.app.compile.lang.common.Primitives;
import magma.app.compile.lang.common.References;
import magma.app.compile.lang.common.Structs;
import magma.app.compile.lang.common.Symbols;
import magma.app.compile.lang.common.TryCatches;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.BackwardsLocator;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.Last;
import magma.app.compile.rule.locate.LocateRule;

import java.util.List;

public class JavaLang {
    public static final String CLASS_TYPE = "class";
    public static final String PACKAGE = "package";
    public static final String CLASS_NAME = "name";
    public static final String METHOD_TYPE = "method";
    public static final String METHOD_DEFINITION = "definition";
    public static final String ARRAY = "array";
    public static final String CONSTRUCTION = "construction";
    public static final String INTERFACE = "interface";

    public static Rule createRootJavaRule() {
        var definition = createDefinitionRule();
        var method = createMethodRule(definition);

        var childRule = new DisjunctionRule(List.of(
                Namespace.createNamespaceRule("package", "package "),
                Namespace.createImportRule(),
                createClassRule(definition, method),
                Structs.createStructRule(INTERFACE, "interface ", method)
        ));

        return new DisjunctionRule(List.of(
                Blocks.createBlockRule(childRule),
                EmptyRule.EMPTY_RULE
        ));
    }

    private static Rule createClassRule(Rule definition, Rule methodRule) {
        var classRule = new LazyRule();
        var modifiers = Modifiers.createModifiersRule();

        var classMember = new DisjunctionRule(List.of(
                methodRule,
                Definitions.createDefinitionStatement(definition),
                classRule
        ));

        var content = new NodeRule("value", Blocks.createBlockRule(classMember));
        var name = new StripRule(new StringRule(CLASS_NAME));

        var leftRule = new DisjunctionRule(List.of(
                new LocateRule(content, new First(" implements "), new StripRule(new StringRule("interface"))),
                name
        ));

        var after = new LocateRule(leftRule, new First("{"), new SuffixRule(content, "}"));
        classRule.set(new TypeRule("class", new LocateRule(modifiers, new First("class "), after)));
        return classRule;
    }

    private static TypeRule createMethodRule(TypeRule definition) {
        var params = new DisjunctionRule(List.of(
                Functions.createParamsRule(definition),
                EmptyRule.EMPTY_RULE
        ));

        var beforeContent = new LocateRule(new NodeRule(METHOD_DEFINITION, definition), new First("("), new StripRule(new SuffixRule(params, ")")));
        var statements = createStatementRule(definition, createValueRule());
        var children = new NodeRule("value", Blocks.createBlockRule(statements));
        var content = new SuffixRule(children, "}");
        var child = new LocateRule(beforeContent, new First("{"), content);

        return new TypeRule(METHOD_TYPE, new DisjunctionRule(List.of(
                child,
                new SuffixRule(beforeContent, ";")
        )));
    }

    private static Rule createStatementRule(Rule definition, Rule value) {
        var statement = new LazyRule();
        statement.set(new DisjunctionRule(List.of(
                PrefixedStatements.createTryRule(statement),
                TryCatches.createCatchRule(definition, statement),
                Declarations.createDeclarationRule(definition, value),
                new TypeRule(CONSTRUCTION, new SuffixRule(createConstructionRule(value), ";")),
                Operations.createInvocationStatementRule(value),
                Comments.createCommentRule(),
                Functions.createReturnRule(value),
                Definitions.createAssignmentRule(value),
                Conditions.createConditionRule("if", "if", value, statement),
                Conditions.createConditionRule("while", "while", value, statement),
                Definitions.createDefinitionStatement(definition),
                Conditions.createElseRule(statement),
                Primitives.createPostRule("decrement", "--", value),
                Primitives.createPostRule("increment", "++", value),
                new TypeRule("continue", new SuffixRule(EmptyRule.EMPTY_RULE, "continue;")),
                new TypeRule("break", new SuffixRule(EmptyRule.EMPTY_RULE, "break;"))
        )));

        return statement;
    }

    private static Rule createValueRule() {
        var value = new LazyRule();
        value.set(new DisjunctionRule(List.of(
                createConstructionRule(value),
                createInvocationRule(value),
                Accesses.createAccessRule(value),
                References.createReferenceRule(),
                Primitives.createStringRule(),
                Operators.createOperatorRule("and", "&&", value),
                Operators.createOperatorRule("equals", "==", value),
                Operators.createOperatorRule("greater-than-or-equals-to", ">=", value),
                Operators.createOperatorRule("less-than", "<", value),
                Primitives.createCharRule(),
                Primitives.createNumberRule(),
                new TypeRule("not", new PrefixRule("!", new NodeRule("value", value)))
        )));
        return value;
    }

    private static TypeRule createInvocationRule(LazyRule value) {
        return Operations.createOperationsRule(Operations.INVOCATION, new NodeRule("caller", value), value);
    }

    private static Rule createConstructionRule(Rule value) {
        var callerProperty = new NodeRule("caller", value);
        var caller = new StripRule(new PrefixRule("new", new StripRule(new DisjunctionRule(List.of(
                new SuffixRule(callerProperty, "<>"),
                callerProperty
        )))));
        return Operations.createOperationsRule(CONSTRUCTION, caller, value);
    }

    private static TypeRule createDefinitionRule() {
        var modifiers = Modifiers.createModifiersRule();
        var type = new NodeRule("type", createTypeRule());

        var modifiersAndType = new DisjunctionRule(List.of(
                new LocateRule(modifiers, new BackwardsLocator(" "), type),
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
