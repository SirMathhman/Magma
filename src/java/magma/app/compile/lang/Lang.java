package magma.app.compile.lang;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.Rule;
import magma.app.compile.NodeResult;
import magma.app.compile.StringResult;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;

public class Lang {
    public static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    public static Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> createImportRule() {
        return new StripRule<>(new PrefixRule<>("import ", new SuffixRule<>(new InfixRule<>(new StringRule("parent"), ".", new StringRule("destination")), ";")));
    }
}
