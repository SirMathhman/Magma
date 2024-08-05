package magma.app.compile.lang;

import magma.app.compile.MemberSplitter;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.First;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
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
                createFunctionRule()
        )));
    }

    private static TypeRule createFunctionRule() {
        var content = CommonLang.createMembersRule(new TypeRule("any", EmptyRule.EMPTY_RULE));
        var definition = new NodeRule(DEFINITION_TYPE, createDefinitionRule());
        return new TypeRule(FUNCTION_TYPE, new LocateRule(definition, new Last(" => "), content));
    }

    private static Rule createDefinitionRule() {
        var modifiers = CommonLang.createModifiersRule();
        var name = new StringRule(DEFINITION_NAME);
        return new TypeRule(DEFINITION_TYPE, new LocateRule(modifiers, new Last(" "), new SuffixRule(name, "()")));
    }
}
