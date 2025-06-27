package magma;

import java.util.stream.Collectors;

public record Definition(ListLike<String> modifiers, String beforeType, String name, String type)
        implements Assignable {
    @Override
    public String generateWithAfterName(final String afterName) {
        final String joinedModifiers;
        if (this.modifiers.isEmpty())
            joinedModifiers = "";
        else
            joinedModifiers = this.modifiers.stream().map(value -> value + " ").collect(Collectors.joining());

        return joinedModifiers + this.name + afterName + " : " + this.type;
    }

    @Override
    public String generate() {
        return this.generateWithAfterName("");
    }

    public Definition withModifier(final String modifier) {
        return new Definition(this.modifiers.add(modifier), this.beforeType, this.name, this.type);
    }
}