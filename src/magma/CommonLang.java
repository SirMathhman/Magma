package magma;

public class CommonLang {
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String MODIFIERS = "modifiers";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    public static final String SEGMENTS = "segments";
    public static final String IMPORT = "import";
    public static final String CHILDREN = "children";

    static Rule createImportRule(String type, String prefix) {
        var segments = new StringRule(SEGMENTS);
        var afterKeyword = new SuffixRule(segments, String.valueOf(Splitter.STATEMENT_END));
        return new TypeRule(type, new PrefixRule(prefix, afterKeyword));
    }
}
