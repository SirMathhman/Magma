package magma.ast;

import magma.util.*;
import magma.compile.*;
public class FunctionType implements Type {
    public final List<Type> parameterTypes;
    public final Type returnType;

    public FunctionType(List<Type> parameterTypes, Type returnType) {
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public String generate() {
        final var parameters = parameterTypes.iterWithIndex()
                .map(entry -> "param" + entry.left + " : " + entry.right.generate())
                .collect(new Joiner(", "))
                .orElse("");

        return "(" + parameters + ") => " + returnType.generate();
    }
}
