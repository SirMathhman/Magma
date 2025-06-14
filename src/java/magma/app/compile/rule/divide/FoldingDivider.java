package magma.app.compile.rule.divide;

import magma.app.compile.rule.divide.state.DivideState;
import magma.app.compile.rule.divide.state.MutableDivideState;

import java.util.List;

public class FoldingDivider {
    static DivideState fold(DivideState state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    public List<String> divide(String input) {
        DivideState current = new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments().toList();
    }
}