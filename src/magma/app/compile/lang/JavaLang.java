package magma.app.compile.lang;

import magma.app.compile.Splitter;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.FirstRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StatementsRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

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
        return new TypeRule(METHOD, new StringRule("temp"));
    }

    private static Rule createClassRule() {
        var modifiers = new StringRule(CommonLang.MODIFIERS);
        var name = new StripRule(new StringRule(CommonLang.NAME));
        var content = new StatementsRule(CommonLang.CONTENT, createClassMembersRule());

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

    public static StatementsRule createRootJavaRule() {
        return new StatementsRule(CommonLang.CHILDREN, createJavaRootMemberRule());
    }
}
