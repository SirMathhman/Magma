package magma.app.compile.lang;

import magma.app.compile.lang.magma.MagmaDefinition;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class MagmaLang {
    public static final String FUNCTION_TYPE = "function";

    public static Rule createRootMagmaRule() {
        return CommonLang.createBlockRule(new DisjunctionRule(List.of(
                CommonLang.createImportRule(),
                createStatementRule0()
        )));
    }

    private static Rule createStatementRule0() {
        var statement = new LazyRule();
        var definition = MagmaDefinition.createRule();
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
                CommonLang.createReferenceRule(),
                CommonLang.createStringRule()
        )));
        return value;
    }

    private static TypeRule createFunctionRule0(Rule definition, Rule statement) {
        var content = new PrefixRule("{", new SuffixRule(new NodeRule("value", CommonLang.createBlockRule(statement)), "}"));
        var definitionProperty = new NodeRule(MagmaDefinition.DEFINITION, definition);
        return new TypeRule(FUNCTION_TYPE, new LocateRule(definitionProperty, new Last(" => "), content));
    }
}
