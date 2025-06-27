package magma;

import java.util.stream.Collectors;

public record Definition(ListLike<String> modifiers, String beforeType, String name, String type)
        implements Assignable {
    @Override
    public String generateWithAfterName(final String afterName) {
        final var joinedModifiers = this.getString();

        return joinedModifiers + this.name + afterName + " : " + this.type;
    }

    private String getString() {
        if (this.modifiers.isEmpty())
            return "";

        return this.modifiers.stream().map(value -> value + " ").collect(Collectors.joining());
    }

    @Override
    public String generate() {
        return this.generateWithAfterName("");
    }

    public Definition withModifier(final String modifier) {
        return new Definition(this.modifiers.add(modifier), this.beforeType, this.name, this.type);
    }
}