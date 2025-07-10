package magma.node;

import java.util.function.Function;

public record Assignment(Definable definition, String value) {
    public Assignment mapDefinition(final Function<Definable, Definable> mapper) {
        final var definition1 = this.definition;
        final var definable2 = mapper.apply(definition1);
        return new Assignment(definable2, this.value());
    }

    public String generate() {
        return this.definition().generate() + " = " + this.value();
    }
}