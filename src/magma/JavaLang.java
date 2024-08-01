package magma;

import magma.rule.DisjunctionRule;
import magma.rule.EmptyRule;
import magma.rule.FirstRule;
import magma.rule.NodeRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StatementsRule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;

import java.util.List;

public class JavaLang {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String VOID_KEYWORD_WITH_SPACE = "void ";
    public static final String METHOD_SUFFIX = "(){}";
    public static final String METHOD = "method";
    public static final String CLASS = "class";
    public static final String PACKAGE = "package";

    static Rule createClassMembersRule() {
        return new DisjunctionRule(List.of(
                new TypeRule("empty", EmptyRule.EMPTY_RULE),
                createMethodRule()
        ));
    }

    private static Rule createMethodRule() {
        return new TypeRule(METHOD, new PrefixRule(VOID_KEYWORD_WITH_SPACE, new SuffixRule(new StringRule(CommonLang.NAME), METHOD_SUFFIX)));
    }

    private static Rule createClassRule() {
        var modifiers = new StringRule(CommonLang.MODIFIERS);
        var name = new StripRule(new StringRule(CommonLang.NAME));
        var content = new NodeRule(CommonLang.CONTENT, createClassMembersRule());

        var contentAndEnd = new SuffixRule(content, String.valueOf(Splitter.BLOCK_END));
        var afterKeyword = new FirstRule(name, String.valueOf(Splitter.BLOCK_START), contentAndEnd);
        return new TypeRule(CLASS, new FirstRule(modifiers, CommonLang.CLASS_KEYWORD_WITH_SPACE, afterKeyword));
    }

    static Rule createJavaRootMemberRule() {
        return new DisjunctionRule(List.of(
                CommonLang.createImportRule(PACKAGE, PACKAGE_KEYWORD_WITH_SPACE),
                CommonLang.createImportRule(CommonLang.IMPORT, CommonLang.IMPORT_KEYWORD_WITH_SPACE),
                createClassRule()
        ));
    }

    static StatementsRule createRootJavaRule() {
        return new StatementsRule(createJavaRootMemberRule(), CommonLang.CHILDREN);
    }
}
