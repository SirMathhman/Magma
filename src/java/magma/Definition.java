package magma;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Definition(Collection<String> modifiers, String name, String type) implements Header, Definable {
    static String generateModifiers(final Collection<String> newModifiers) {
        return newModifiers.stream().map(value -> value + " ").collect(Collectors.joining());
    }

    @Override
    public String generate() {
        return this.generateWithAfterName("");
    }

    @Override
    public String generateWithAfterName(final String afterName) {
        final var joinedModifiers = Definition.generateModifiers(this.modifiers());
        return joinedModifiers + this.name() + afterName + " : " + this.type();
    }

    public Definition mapModifiers(final Function<Collection<String>, Collection<String>> mapper) {
        return new Definition(mapper.apply(this.modifiers), this.name, this.type);
    }
}