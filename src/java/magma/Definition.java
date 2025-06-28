package magma;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Definition(ListLike<String> annotations, ListLike<String> modifiers, List<String> typeParams, String name,
                         String type) implements Assignable {
    @Override
    public String generateWithAfterName(final String afterName) {
        final String joinedTypeParams;
        if (this.typeParams.isEmpty())
            joinedTypeParams = "";
        else
            joinedTypeParams = "<" + String.join(", ", this.typeParams) + ">";
        final var joinedModifiers = this.getString();
        return joinedModifiers + this.name + joinedTypeParams + afterName + " : " + this.type;
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
        return new Definition(this.annotations, this.modifiers.add(modifier), this.typeParams, this.name, this.type);
    }

    public Definition mapModifiers(final Function<ListLike<String>, ListLike<String>> mapper) {
        return new Definition(this.annotations, mapper.apply(this.modifiers), this.typeParams, this.name, this.type);
    }
}