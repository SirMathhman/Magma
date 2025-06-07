package magma.ast;

import magma.util.*;
import magma.Generator;
import magma.compile.*;
public record TemplateType(String base, List<Type> arguments) implements Type {
    @Override
    public String generate() {
        final var outputArguments = Generator.generateNodes(arguments);
        return base + "<" + outputArguments + ">";
    }
}
