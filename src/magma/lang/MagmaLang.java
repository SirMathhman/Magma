package magma.lang;

import magma.Splitter;
import magma.app.rule.DisjunctionRule;
import magma.app.rule.EmptyRule;
import magma.app.rule.FirstRule;
import magma.app.rule.LazyRule;
import magma.app.rule.NodeRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.Rule;
import magma.app.rule.StatementsRule;
import magma.app.rule.StringRule;
import magma.app.rule.SuffixRule;
import magma.app.rule.TypeRule;

import java.util.List;

public class MagmaLang {
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String FUNCTION = "function";

    static Rule createMagmaRootMemberRule() {
        return new DisjunctionRule(List.of(
                CommonLang.createImportRule(CommonLang.IMPORT, CommonLang.IMPORT_KEYWORD_WITH_SPACE),
                createStatementRule())
        );
    }

    static Rule createStatementRule() {
        var statement = new LazyRule();
        statement.set(new DisjunctionRule(List.of(
                createFunctionRule(statement),
                EmptyRule.EMPTY_RULE
        )));

        return statement;
    }

    static Rule createFunctionRule(Rule statement) {
        var modifiers = new DisjunctionRule(List.of(new StringRule(CommonLang.MODIFIERS), EmptyRule.EMPTY_RULE));
        var name = new StringRule(CommonLang.NAME);
        var content = new DisjunctionRule(List.of(new NodeRule(CommonLang.CONTENT, statement), EmptyRule.EMPTY_RULE));
        var wrappedContent = new PrefixRule(String.valueOf(Splitter.BLOCK_START), new SuffixRule(content, String.valueOf(Splitter.BLOCK_END)));
        var right = new FirstRule(name, "() => ", wrappedContent);
        return new TypeRule(FUNCTION, new FirstRule(modifiers, "def ", right));
    }

    static StatementsRule createRootMagmaRule() {
        return new StatementsRule(createMagmaRootMemberRule(), CommonLang.CHILDREN);
    }
}
