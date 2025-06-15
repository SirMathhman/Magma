package magma.app.compile;

import magma.app.compile.lang.Lang;
import magma.app.compile.string.StringResults;
import magma.app.compile.node.PresentNodeListResult;
import magma.app.compile.rule.DivideState;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.StringRule;

import java.util.List;

public class Compiler {
    public static StringResult<CompileError> compileRoot(String input, String name) {
        return lex(input, new OrRule<Node>(List.of(Lang.createImportRule(), new StringRule("value")))).transform(children -> transform(name, children)).generate(Compiler::generate);
    }

    static List<String> divide(String input) {
        var current = new DivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments();
    }

    static DivideState fold(DivideState state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    public static StringResult<CompileError> generate(List<Node> children) {
        return children.stream().map(node -> new OrRule<Node>(List.of(Lang.createDependencyRule(), new EmptyRule())).generate(node)).<StringResult<CompileError>>reduce(StringResults.createFromValue(""), (compileErrorOkStringResult, other) -> compileErrorOkStringResult.appendMaybe(other), (_, next) -> next);
    }

    public static List<Node> transform(String name, List<Node> list) {
        return list.stream().map(node -> node.withString("source", name)).toList();
    }

    public static NodeListResult<Node, CompileError> lex(String input, Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> rule) {
        return divide(input).stream().map(rule::lex).<NodeListResult<Node, CompileError>>reduce(new PresentNodeListResult<Node, CompileError>(), NodeListResult::add, (_, next) -> next);
    }
}