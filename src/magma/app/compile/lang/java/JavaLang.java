package magma.app.compile.lang.java;

import magma.app.compile.lang.SymbolRule;
import magma.app.compile.lang.common.Accesses;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.Comments;
import magma.app.compile.lang.common.Conditions;
import magma.app.compile.lang.common.Declarations;
import magma.app.compile.lang.common.Definitions;
import magma.app.compile.lang.common.Functions;
import magma.app.compile.lang.common.Generics;
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
import magma.app.compile.rule.NodeListRule;
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
import magma.app.compile.split.ParamSplitter;

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
    public static final String LAMBDA = "lambda";

    public static Rule createRootJavaRule() {
        var type = createTypeRule();
        var definition = createDefinitionRule(type);
        var statement = new LazyRule();

        var value = createValueRule(type, definition);
        initStatementRule(definition, value, type, statement);
        var method = createMethodRule(definition, statement);

        var childRule = new DisjunctionRule(List.of(
                Namespace.createNamespaceRule("package", "package "),
                Namespace.createImportRule(),
                createClassRule(definition, method, value, type),
                Structs.createStructRule(INTERFACE, "interface ", method)
        ));

        return new DisjunctionRule(List.of(
                Blocks.createBlockRule(childRule),
                EmptyRule.EMPTY_RULE
        ));
    }

    private static Rule createClassRule(Rule definition, Rule methodRule, Rule value, Rule type) {
        var classRule = new LazyRule();
        var modifiers = Modifiers.createModifiersRule();

        var classMember = new DisjunctionRule(List.of(
                methodRule,
                Definitions.createDefinitionStatement(definition),
                Declarations.createDeclarationRule(definition, value),
                classRule
        ));

        var content = new NodeRule("value", Blocks.createBlockRule(classMember));
        var name = new StripRule(new StringRule(CLASS_NAME));
        var paramsRule = new NodeListRule(new ParamSplitter(), "type-params", Symbols.createSymbolRule());

        var maybeTypeParams = new DisjunctionRule(List.of(
                new LocateRule(name, new First("<"), new StripRule(new SuffixRule(paramsRule, ">"))),
                name
        ));

        var leftRule = new DisjunctionRule(List.of(
                new LocateRule(maybeTypeParams, new First(" implements "), new NodeRule("interface", type)),
                maybeTypeParams
        ));

        var after = new LocateRule(leftRule, new First("{"), new SuffixRule(content, "}"));
        classRule.set(new TypeRule("class", new LocateRule(modifiers, new First("class "), after)));
        return classRule;
    }

    private static Rule createMethodRule(Rule definition, Rule statements) {
        var params = new DisjunctionRule(List.of(
                Functions.createParamsRule(definition),
                EmptyRule.EMPTY_RULE
        ));

        var beforeContent = new LocateRule(new NodeRule(METHOD_DEFINITION, definition), new First("("), new StripRule(new SuffixRule(params, ")")));
        var children = new NodeRule("value", Blocks.createBlockRule(statements));
        var content = new SuffixRule(children, "}");
        var child = new LocateRule(beforeContent, new First("{"), content);

        return new TypeRule(METHOD_TYPE, new DisjunctionRule(List.of(
                child,
                new SuffixRule(beforeContent, ";")
        )));
    }

    private static Rule initStatementRule(Rule definition, Rule value, Rule type, LazyRule statement) {
        statement.set(new DisjunctionRule(List.of(
                PrefixedStatements.createTryRule(statement),
                TryCatches.createCatchRule(definition, statement),
                Declarations.createDeclarationRule(definition, value),
                new TypeRule(CONSTRUCTION, new SuffixRule(createConstructionRule(value, type), ";")),
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
                Conditions.createContinueRule(),
                Conditions.createBreakRule()
        )));

        return statement;
    }

    private static Rule createValueRule(Rule type, Rule definition) {
        var value = new LazyRule();
        value.set(new DisjunctionRule(List.of(
                createConstructionRule(value, type),
                createInvocationRule(value),
                Accesses.createAccessRule(value),
                References.createReferenceRule(),
                Primitives.createStringRule(),
                Operators.createOperatorRule("and", "&&", value),
                Operators.createOperatorRule("equals", "==", value),
                Operators.createOperatorRule("greater-than-or-equals-to", ">=", value),
                Operators.createOperatorRule("less-than", "<", value),
                Operators.createOperatorRule("add", "+", value),
                Operators.createOperatorRule("subtract", "-", value),
                Primitives.createCharRule(),
                Primitives.createNumberRule(),
                Primitives.createNotRule(value),
                Conditions.createTernaryRule(value),
                createLambdaRule(value, definition)
        )));
        return value;
    }

    private static TypeRule createLambdaRule(Rule value, Rule definition) {
        var paramsParentheses = new StripRule(new PrefixRule("(", new SuffixRule(Functions.createParamsRule(definition), ")")));
        var params = new DisjunctionRule(List.of(
                paramsParentheses,
                new StripRule(new SymbolRule(new StringRule("param")))
        ));

        return new TypeRule(LAMBDA, new LocateRule(params, new First("->"), new NodeRule("value", value)));
    }

    private static TypeRule createInvocationRule(LazyRule value) {
        return Operations.createOperationsRule(Operations.INVOCATION, new NodeRule("caller", value), value);
    }

    private static Rule createConstructionRule(Rule value, Rule type) {
        var callerProperty = new NodeRule("caller", value);
        var arguments = new NodeListRule(new ParamSplitter(), "type-arguments", type);
        var caller = new StripRule(new PrefixRule("new", new StripRule(new DisjunctionRule(List.of(
                new LocateRule(callerProperty, new First("<"), new StripRule(new SuffixRule(arguments, ">"))),
                callerProperty
        )))));
        return Operations.createOperationsRule(CONSTRUCTION, caller, value);
    }

    private static TypeRule createDefinitionRule(Rule type) {
        var modifiers = Modifiers.createModifiersRule();
        var typeProperty = new NodeRule("type", type);

        var modifiersAndType = new DisjunctionRule(List.of(
                new LocateRule(modifiers, new BackwardsLocator(" "), typeProperty),
                typeProperty
        ));

        var name = new StringRule(CLASS_NAME);
        return new TypeRule(METHOD_DEFINITION, new StripRule(new LocateRule(modifiersAndType, new Last(" "), name)));
    }

    private static Rule createTypeRule() {
        var type = new LazyRule();
        type.set(new DisjunctionRule(List.of(
                Generics.createGenericRule(type),
                new TypeRule(ARRAY, new SuffixRule(new NodeRule("child", type), "[]")),
                Symbols.createSymbolRule()
        )));
        return type;
    }

}
