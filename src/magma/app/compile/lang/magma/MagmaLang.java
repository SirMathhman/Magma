package magma.app.compile.lang.magma;

import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.CommonLang;
import magma.app.compile.lang.common.Declarations;
import magma.app.compile.lang.common.PrefixedStatements;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.Rule;

import java.util.List;

public class MagmaLang {
    public static Rule createRootMagmaRule() {
        return Blocks.createBlockRule(new DisjunctionRule(List.of(
                CommonLang.createImportRule(),
                createStatementRule0()
        )));
    }

    private static Rule createStatementRule0() {
        var statement = new LazyRule();
        var definition = MagmaDefinition.createRule();
        var value = createValueRule();

        statement.set(new DisjunctionRule(List.of(
                Functions.createFunctionRule0(definition, statement),
                PrefixedStatements.createTryRule(statement),
                CommonLang.createCatchRule(definition, statement),
                Declarations.createDeclarationRule(definition, value),
                CommonLang.createInvocationStatementRule(value),
                CommonLang.createCommentRule(),
                CommonLang.createReturnRule(value),
                Objects.createObjectRule(statement)
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

}