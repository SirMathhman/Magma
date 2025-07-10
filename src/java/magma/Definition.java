package magma;

import java.util.Collection;
import java.util.stream.Collectors;

public record Definition(Collection<String> newModifiers, String name, String type) implements Header {
    static String generateModifiers(final Collection<String> newModifiers) {
        return newModifiers.stream().map(value -> value + " ").collect(Collectors.joining());
    }

    @Override
    public String generate() {
        return this.generateWithAfterName("");
    }

    @Override
    public String generateWithAfterName(final String afterName) {
        final var joinedModifiers = Definition.generateModifiers(this.newModifiers());
        return joinedModifiers + this.name() + afterName + " : " + this.type();
    }
}