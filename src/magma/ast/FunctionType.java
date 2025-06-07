package magma.ast;

import magma.util.*;

public record FunctionType(List<Type> parameterTypes, Type returnType) implements Type {

    @Override
    public String generate() {
        final var parameters = parameterTypes.iterWithIndex()
                .map(entry -> "param" + entry.left() + " : " + entry.right().generate())
                .collect(new Joiner(", "))
                .orElse("");

        return "(" + parameters + ") => " + returnType.generate();
    }
}
