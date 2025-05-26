package magma.app.compile.rule;

import magma.api.collect.head.HeadedIter;
import magma.api.collect.head.RangeHead;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.text.Characters;
import magma.api.text.Strings;
import magma.app.compile.node.Node;

public record SymbolRule(TypeRule childRule) implements Rule<Node> {
    public static boolean isSymbol(String input) {
        var query = new HeadedIter<Integer>(new RangeHead(Strings.length(input)));
        return query.allMatch((Integer index) -> {
            return SymbolRule.isSymbolChar(index, input.charAt(index));
        });
    }

    private static boolean isSymbolChar(int index, char c) {
        return '_' == c
                || Characters.isLetter(c)
                || (0 != index && Characters.isDigit(c));
    }

    @Override
    public Option<Node> lex(String input) {
        if (SymbolRule.isSymbol(input)) {
            return this.childRule().lex(input);
        }

        return new None<>();
    }
}