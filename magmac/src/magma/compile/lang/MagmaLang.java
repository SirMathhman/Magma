package magma.compile.lang;

import magma.compile.rule.EmptyRule;
import magma.compile.rule.LazyRule;
import magma.compile.rule.OrRule;
import magma.compile.rule.Rule;
import magma.compile.rule.TypeRule;
import magma.compile.rule.split.FirstRule;
import magma.compile.rule.split.LastRule;
import magma.compile.rule.text.LeftRule;
import magma.compile.rule.text.RightRule;
import magma.compile.rule.text.extract.ExtractNodeRule;
import magma.compile.rule.text.extract.ExtractStringRule;

import java.util.List;

public class MagmaLang {

    public static Rule createRootRule() {
        var statement = new LazyRule();

        var definition = createDefinitionRule();

        var value = new LeftRule("?", new EmptyRule());
        statement.setRule(new OrRule(List.of(
                Lang.createCommentRule(),
                Lang.createTryRule(statement),
                Lang.createCatchRule(definition, statement),
                Lang.createIfRule(value, statement),
                Lang.createElseRule(statement),
                Lang.createReturnRule(value),
                Lang.createAssignmentRule(value),
                Lang.createForRule(definition, value, statement, " in "),
                createFunctionRule(statement),
                new TypeRule("declaration", new RightRule(definition, ";")),
                Lang.createInvocationRule(value)
        )));

        return Lang.createBlock(new OrRule(List.of(
                Lang.createImportRule(Lang.createNamespaceRule()),
                statement)));
    }

    private static Rule createDefinitionRule() {
        var modifiers = Lang.createModifiersRule();
        var withoutModifiers = new ExtractStringRule("name");
        var withModifiers = new LastRule(modifiers, " ", withoutModifiers);

        var maybeModifiers = new OrRule(List.of(
                withModifiers,
                withoutModifiers
        ));

        var type = Lang.createTypeRule();
        var withoutType = maybeModifiers;
        var withType = new LastRule(maybeModifiers, " : " , new ExtractNodeRule("type", type));
        var maybeType = new OrRule(List.of(withType, withoutType));

        return maybeType;
    }

    private static TypeRule createFunctionRule(Rule statement) {
        var child = new ExtractNodeRule("child", Lang.createBlock(statement));
        return new TypeRule("function", new FirstRule(createDefinitionRule(), " => {", new RightRule(child, "}")));
    }
}
