package magma.ast;

import magma.util.*;
import magma.Main;
import magma.compile.*;
public record TemplateType(String base, List<Type> arguments) implements Type {
    @Override
    public String generate() {
        final var outputArguments = Main.generateNodes(arguments);
        return base + "<" + outputArguments + ">";
    }
}
