package magma;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Definition(Collection<String> modifiers, String name, Optional<String> maybeType)
        implements Header, Definable {
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
        return joinedModifiers + this.name() + afterName + this.maybeType.map(value -> " : " + value).orElse("");
    }

    public Definition mapModifiers(final Function<Collection<String>, Collection<String>> mapper) {
        return new Definition(mapper.apply(this.modifiers), this.name, this.maybeType);
    }

    public Definable mapType(final Function<String, Optional<String>> mapper) {
        return new Definition(this.modifiers, this.name, this.maybeType.map(mapper).orElse(this.maybeType));
    }
}