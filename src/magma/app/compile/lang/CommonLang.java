package magma.app.compile.lang;

import magma.app.compile.MemberSplitter;
import magma.app.compile.ParamSplitter;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
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
import java.util.function.Function;

public class CommonLang {
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String MODIFIERS = "modifiers";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    public static final String CHILDREN = "children";
    public static final String BEFORE_CHILD = "before-child";
    public static final String AFTER_CHILD = "after-child";
    public static final String BLOCK_TYPE = "block";
    public static final String BEFORE_CHILDREN = "before-children";
    public static final String AFTER_CHILDREN = "after-children";
    public static final String DECLARATION_TYPE = "declaration";
    public static final String DECLARATION_DEFINITION = "definition";
    public static final String PARAMS = "params";

    static Rule createMembersRule(Rule childRule) {
        var wrappedChild = new StripRule(childRule, BEFORE_CHILD, AFTER_CHILD);
        var property = new NodeListRule(new MemberSplitter(), "children", wrappedChild);
        return new StripRule(property, BEFORE_CHILDREN, AFTER_CHILDREN);
    }

    static Rule createBlockRule(Rule childRule) {
        return new TypeRule(BLOCK_TYPE, createMembersRule(childRule));
    }

    static Rule createImportRule() {
        return createNamespaceRule("import", "import ");
    }

    static Rule createNamespaceRule(String type, String prefix) {
        return new TypeRule(type, new PrefixRule(prefix, new SuffixRule(new StringListRule("namespace", "."), ";")));
    }

    static StripRule createModifiersRule() {
        return new StripRule(new StringListRule(MODIFIERS, " "));
    }

    static TypeRule createTryRule(Rule statement) {
        return createPrefixedStatementRule("try", "try", statement, rule -> rule);
    }

    static TypeRule createPrefixedStatementRule(String type, String prefix, Rule statement, Function<Rule, Rule> function) {
        var children = new NodeRule("value", createBlockRule(createMembersRule(statement)));
        var childrenProperty = new StripRule(new PrefixRule("{", new SuffixRule(children, "}")));
        return new TypeRule(type, new PrefixRule(prefix, function.apply(childrenProperty)));
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
        var arguments = new DisjunctionRule(List.of(
                new NodeListRule(new ParamSplitter(), "arguments", value),
                EmptyRule.EMPTY_RULE
        ));

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
        return createPrefixedStatementRule("catch", "catch", statement, children -> captureCatchParameters(children, definition));
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

    static NodeListRule createParamsRule(Rule definition) {
        return new NodeListRule(new ParamSplitter(), PARAMS, definition);
    }
}
