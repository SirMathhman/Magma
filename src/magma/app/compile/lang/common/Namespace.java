package magma.app.compile.lang.common;

import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringListRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public class Namespace {
    public static Rule createImportRule() {
        return createNamespaceRule("import", "import ");
    }

    public static Rule createNamespaceRule(String type, String prefix) {
        return new TypeRule(type, new PrefixRule(prefix, new SuffixRule(new StringListRule("namespace", "."), ";")));
    }
}
