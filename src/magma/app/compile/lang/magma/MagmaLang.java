package magma.app.compile.lang.magma;

import magma.app.compile.lang.common.Accesses;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.Comments;
import magma.app.compile.lang.common.Conditions;
import magma.app.compile.lang.common.Declarations;
import magma.app.compile.lang.common.Definitions;
import magma.app.compile.lang.common.Namespace;
import magma.app.compile.lang.common.Operations;
import magma.app.compile.lang.common.Operators;
import magma.app.compile.lang.common.PrefixedStatements;
import magma.app.compile.lang.common.Primitives;
import magma.app.compile.lang.common.References;
import magma.app.compile.lang.common.Structs;
import magma.app.compile.lang.common.TryCatches;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.Rule;

import java.util.List;

public class MagmaLang {

    public static final String TRAIT = "trait";

    public static Rule createRootMagmaRule() {
        return new DisjunctionRule(List.of(
                Blocks.createBlockRule(new DisjunctionRule(List.of(
                        Namespace.createImportRule(),
                        createStatementRule0()
                )))
        ));
    }

    private static Rule createStatementRule0() {
        var statement = new LazyRule();
        var definition = MagmaDefinition.createRule();
        var value = createValueRule();

        statement.set(new DisjunctionRule(List.of(
                Functions.createFunctionRule(definition, statement),
                PrefixedStatements.createTryRule(statement),
                TryCatches.createCatchRule(definition, statement),
                Declarations.createDeclarationRule(definition, value),
                Operations.createInvocationStatementRule(value),
                Comments.createCommentRule(),
                magma.app.compile.lang.common.Functions.createReturnRule(value),
                Objects.createObjectRule(statement),
                Definitions.createDefinitionStatement(definition),
                Definitions.createAssignmentRule(value),
                Conditions.createConditionRule("if", "if", value, statement),
                Conditions.createConditionRule("while", "while", value, statement),
                Conditions.createElseRule(statement),
                Primitives.createPostRule("decrement", "--", value),
                Structs.createStructRule(TRAIT, "trait ", statement)
        )));

        return statement;
    }

    private static Rule createValueRule() {
        var value = new LazyRule();
        value.set(new DisjunctionRule(List.of(
                Operations.createInvocationRule(value),
                Accesses.createAccessRule(value),
                References.createReferenceRule(),
                Primitives.createStringRule(),
                Primitives.createNumberRule(),
                Operators.createOperatorRule("and", "&&", value),
                Operators.createOperatorRule("equals", "==", value),
                Operators.createOperatorRule("greater-than-or-equals-to", ">=", value),
                Primitives.createCharRule()
        )));
        return value;
    }

}
