package magma.app.compile.lang;

import magma.app.compile.Splitter;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.FirstRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StatementsRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class MagmaLang {
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String FUNCTION = "function";

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
        var wrappedContent = new PrefixRule(String.valueOf(Splitter.BLOCK_START), new SuffixRule(content, String.valueOf(Splitter.BLOCK_END)));
        var right = new FirstRule(name, "() => ", wrappedContent);
        return new TypeRule(FUNCTION, new FirstRule(modifiers, "def ", right));
    }

    public static Rule createRootMagmaRule() {
        return EmptyRule.EMPTY_RULE;
    }
}