package magma.app.compile.lang;

import magma.app.compile.ParamSplitter;
import magma.app.compile.lang.common.PrefixedStatements;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.First;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.OptionalRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringListRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public class CommonLang {
    public static final String MODIFIERS = "modifiers";
    public static final String DECLARATION_TYPE = "declaration";
    public static final String DECLARATION_DEFINITION = "definition";
    public static final String PARAMS = "params";

    static Rule createImportRule() {
        return createNamespaceRule("import", "import ");
    }

    static Rule createNamespaceRule(String type, String prefix) {
        return new TypeRule(type, new PrefixRule(prefix, new SuffixRule(new StringListRule("namespace", "."), ";")));
    }

    public static StripRule createModifiersRule() {
        return new StripRule(new StringListRule(MODIFIERS, " "));
    }

    static TypeRule createDeclarationRule(Rule definitionRule, Rule valueRule) {
        var definition = new NodeRule(DECLARATION_DEFINITION, definitionRule);
        var value = new NodeRule("value", valueRule);
        return new TypeRule(DECLARATION_TYPE, new LocateRule(definition, new Last("="), new SuffixRule(value, ";")));
    }

    static TypeRule createInvocationRule(Rule value) {
        return createOperationsRule(value, new NodeRule("caller", value));
    }

    static TypeRule createOperationsRule(Rule value, Rule caller) {
        var arguments = new OptionalRule("arguments",
                new NodeListRule(new ParamSplitter(), "arguments", value),
                EmptyRule.EMPTY_RULE
        );

        return new TypeRule("invocation", new LocateRule(caller, new Last("("), new StripRule(new SuffixRule(arguments, ")"))));
    }

    static TypeRule createReferenceRule() {
        return new TypeRule("reference", new StripRule(new SymbolRule(new StringRule("value"))));
    }

    static TypeRule createInvocationStatementRule(Rule value) {
        return new TypeRule("invocation", new SuffixRule(createInvocationRule(value), ";"));
    }

    static TypeRule createAccessRule(LazyRule value) {
        return new TypeRule("access", new LocateRule(new NodeRule("object", value), new Last("."), new StringRule("member")));
    }

    static TypeRule createCatchRule(Rule definition, LazyRule statement) {
        return PrefixedStatements.createPrefixedStatementRule("catch", "catch", statement, children -> captureCatchParameters(children, definition));
    }

    private static Rule captureCatchParameters(Rule children, Rule definition) {
        var params = new NodeListRule(new ParamSplitter(), "params", definition);
        return new StripRule(new PrefixRule("(", new LocateRule(params, new First(")"), children)));
    }

    static TypeRule createCommentRule() {
        return new TypeRule("comment", new PrefixRule("//", new StringRule("value")));
    }

    static TypeRule createReturnRule(Rule value) {
        return new TypeRule("return", new PrefixRule("return ", new SuffixRule(new NodeRule("value", value), ";")));
    }

    public static NodeListRule createParamsRule(Rule definition) {
        return new NodeListRule(new ParamSplitter(), PARAMS, definition);
    }

    static TypeRule createStringRule() {
        return new TypeRule("string", new StripRule(new PrefixRule("\"", new SuffixRule(new StringRule("value"), "\""))));
    }
}
