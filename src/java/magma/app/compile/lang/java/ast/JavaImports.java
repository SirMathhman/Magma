package magma.app.compile.lang.java.ast;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;

public class JavaImports {
    public static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createImportRule() {
        final var parent = new StringRule("parent");
        final var destination = new StringRule("destination");
        return new StripRule<>(new PrefixRule<>("import ", new SuffixRule<>(new InfixRule<>(parent, ".", destination), ";")));
    }
}
