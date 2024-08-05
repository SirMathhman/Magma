package magma.app.compile.lang;

import magma.app.compile.MemberSplitter;
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
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class MagmaLang {
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String FUNCTION_TYPE = "function";
    public static final String DEFINITION_NAME = "name";
    public static final String DEFINITION_TYPE = "definition";

    public static Rule createStatementRule() {
        var statement = new LazyRule();
        statement.set(new DisjunctionRule(List.of(
                createFunctionRule(statement),
                EmptyRule.EMPTY_RULE
        )));

        return statement;
    }

    public static Rule createFunctionRule(Rule statement) {
        var modifiers = new DisjunctionRule(List.of(new StringRule(CommonLang.MODIFIERS), EmptyRule.EMPTY_RULE));
        var name = new StringRule(CommonLang.NAME);
        var content = new DisjunctionRule(List.of(new NodeRule(CommonLang.CONTENT, statement), EmptyRule.EMPTY_RULE));
        var wrappedContent = new PrefixRule(String.valueOf(MemberSplitter.BLOCK_START), new SuffixRule(content, String.valueOf(MemberSplitter.BLOCK_END)));
        var right = new LocateRule(name, new First("() => "), wrappedContent);
        return new TypeRule(FUNCTION_TYPE, new LocateRule(modifiers, new First("def "), right));
    }

    public static Rule createRootMagmaRule() {
        return CommonLang.createBlockRule(new DisjunctionRule(List.of(
                CommonLang.createImportRule(),
                createStatementRule0()
        )));
    }

    private static Rule createStatementRule0() {
        var statement = new LazyRule();
        var definition = createDefinitionRule();
        var value = createValueRule();

        statement.set(new DisjunctionRule(List.of(
                createFunctionRule0(definition, statement),
                CommonLang.createTryRule(statement),
                CommonLang.createCatchRule(definition, statement),
                CommonLang.createDeclarationRule(definition, value),
                CommonLang.createInvocationStatementRule(value),
                CommonLang.createCommentRule(),
                CommonLang.createReturnRule(value)
        )));
        return statement;
    }

    private static Rule createValueRule() {
        var value = new LazyRule();
        value.set(new DisjunctionRule(List.of(
                CommonLang.createInvocationRule(value),
                CommonLang.createAccessRule(value),
                CommonLang.createReferenceRule()
        )));
        return value;
    }

    private static TypeRule createFunctionRule0(Rule definition, Rule statement) {
        var content = new PrefixRule("{", new SuffixRule(new NodeRule("value", CommonLang.createBlockRule(statement)), "}"));
        var definitionProperty = new NodeRule(DEFINITION_TYPE, definition);
        return new TypeRule(FUNCTION_TYPE, new LocateRule(definitionProperty, new Last(" => "), content));
    }

    private static Rule createDefinitionRule() {
        var definition = new LazyRule();
        var name = new StringRule(DEFINITION_NAME);
        var params = new SuffixRule(CommonLang.createParamsRule(definition), ")");

        var name1 = new DisjunctionRule(List.of(
                new LocateRule(name, new First("("), params),
                name
        ));

        var withModifiers = new LocateRule(CommonLang.createModifiersRule(), new Last(" "), name1);
        definition.set(new TypeRule(DEFINITION_TYPE, new DisjunctionRule(List.of(
                withModifiers,
                name1
        ))));
        return definition;
    }
}
