package magma.app.compile.lang.java;

import magma.app.compile.SimpleRule;
import magma.app.compile.lang.java.ast.JavaImports;
import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.StringRule;

import java.util.List;

public class JavaLang {
    public static SimpleRule createJavaRootRule() {
        return new DivideRule("children", new OrRule(List.of(JavaImports.createImportRule(), new StringRule("value"))));
    }
}
