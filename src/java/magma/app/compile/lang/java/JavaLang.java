package magma.app.compile.lang.java;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;
import magma.app.compile.lang.java.ast.JavaImports;
import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.StringRule;

import java.util.List;

public class JavaLang {
    public static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createJavaRootRule() {
        return new DivideRule("children", new OrRule(List.of(JavaImports.createImportRule(), new StringRule("value"))));
    }
}
