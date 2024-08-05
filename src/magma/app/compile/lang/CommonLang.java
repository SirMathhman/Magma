package magma.app.compile.lang;

import magma.app.compile.MemberSplitter;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringListRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public class CommonLang {
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String MODIFIERS = "modifiers";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    public static final String SEGMENTS = "segments";
    public static final String CHILDREN = "children";

    static Rule createImportRule(String type, String prefix) {
        var segments = new StringRule(SEGMENTS);
        var afterKeyword = new SuffixRule(segments, String.valueOf(MemberSplitter.STATEMENT_END));
        return new TypeRule(type, new PrefixRule(prefix, afterKeyword));
    }

    static NodeListRule createMembersRule(Rule childRule) {
        return new NodeListRule(new MemberSplitter(), "children", childRule);
    }

    static Rule createBlockRule(Rule childRule) {
        return new TypeRule("block", createMembersRule(childRule));
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
}
