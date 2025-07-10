package magma;

import java.util.function.Function;

public record Assignment(Definable definition, String value) {
    Assignment mapDefinition(final Function<Definable, Definable> mapper) {
        final var definition1 = this.definition;
        final var definable2 = mapper.apply(definition1);
        return new Assignment(definable2, this.value());
    }

    String generate() {
        return this.definition().generate() + " = " + this.value();
    }
}