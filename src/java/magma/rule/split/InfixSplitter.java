package magma.rule.split;

import magma.api.Tuple;
import magma.rule.locate.LastLocator;
import magma.rule.locate.Locator;

import java.util.Optional;

public class InfixSplitter implements Splitter {
    private final String infix;
    private final Locator locator;

    public InfixSplitter(final String infix, final LastLocator locator) {
        this.infix = infix;
        this.locator = locator;
    }

    @Override
    public Optional<Tuple<String, String>> split(final String input) {
        return this.locator.locate(input, this.infix).flatMap(index -> {
            final var infixLength = this.infix.length();
            final var leftSlice = input.substring(0, index);
            final var rightSlice = input.substring(index + infixLength);
            return Optional.of(new Tuple<>(leftSlice, rightSlice));
        });
    }
}