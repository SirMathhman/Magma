package magma.app;

import magma.app.maybe.NodeListResult;
import magma.app.maybe.NodeResult;
import magma.app.maybe.StringResult;
import magma.app.maybe.node.PresentNodeListResult;
import magma.app.maybe.string.OkStringResult;
import magma.app.rule.EmptyRule;
import magma.app.rule.InfixRule;
import magma.app.rule.OrRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

import java.util.List;

public class Compiler {
    public static String compileRoot(String input, String name) {
        return lex(input, new OrRule(List.of(createImportRule(), new StringRule("value")))).transform(children -> transform(name, children)).generate(Compiler::generate).orElse("");
    }

    static Rule<Node, NodeResult, StringResult> createDependencyRule() {
        return new SuffixRule<Node, StringResult>(new InfixRule<Node, StringResult>(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    static Rule<Node, NodeResult, StringResult> createImportRule() {
        return new StripRule<Node, NodeResult, StringResult>(new PrefixRule<Node, StringResult>("import ", new SuffixRule<Node, StringResult>(new InfixRule<Node, StringResult>(new StringRule("parent"), ".", new StringRule("destination")), ";")));
    }

    static List<String> divide(String input) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments();
    }

    static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';') {
            return appended.advance();
        }
        return appended;
    }

    public static StringResult generate(List<Node> children) {
        return children.stream().map(node -> new OrRule(List.of(createDependencyRule(),
                new EmptyRule()
        )).generate(node)).reduce(new OkStringResult(""), StringResult::appendMaybe, (_, next) -> next);
    }

    public static List<Node> transform(String name, List<Node> list) {
        return list.stream().map(node -> node.withString("source", name)).toList();
    }

    public static NodeListResult lex(String input, Rule<Node, NodeResult, StringResult> rule) {
        return divide(input).stream().map(rule::lex).reduce(new PresentNodeListResult(), NodeListResult::add, (_, next) -> next);
    }
}