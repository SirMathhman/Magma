package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeListResult;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;
import magma.app.compile.node.NodeListOk;
import magma.app.compile.string.Appending;
import magma.app.compile.string.StringResults;

import java.util.Collection;
import java.util.List;

public record DivideRule(String key,
                         Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> rule) implements Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> {
    private static StringResult<CompileError> getReduce(Collection<Node> children, Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> rule) {
        return children.stream()
                .map(rule::generate)
                .reduce(StringResults.createFromValue(""), Appending::appendMaybe, (_, next) -> next);
    }

    public static List<String> divide(CharSequence input) {
        var current = new DivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    static DivideState fold(DivideState state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    @Override
    public StringResult<CompileError> generate(Node node) {
        return node.findNodeList(this.key)
                .generate(children -> getReduce(children, this.rule));
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        return divide(input).stream()
                .map(this.rule::lex)
                .<NodeListResult<Node, CompileError, NodeResult<Node, CompileError>>>reduce(new NodeListOk(), NodeListResult::add, (_, next) -> next)
                .toNode(this.key);
    }
}