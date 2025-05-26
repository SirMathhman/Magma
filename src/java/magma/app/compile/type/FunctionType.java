package magma.app.compile.type;

import magma.api.Tuple2;
import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.app.TypeCompiler;

public record FunctionType(Iterable<String> args, String returns) implements Type {
    public String generateType() {
        var joinedArguments = this.args
                .iterWithIndices()
                .map((Tuple2<Integer, String> tuple) -> {
                    return "arg" + tuple.left() + " : " + tuple.right();
                })
                .collect(new Joiner(", "))
                .orElse("");

        return "(" + joinedArguments + ") => " + this.returns;
    }

    public boolean isFunctional() {
        return true;
    }

    public boolean isVar() {
        return false;
    }

    public String generateBeforeName() {
        return "";
    }

    public String generateSimple() {
        return TypeCompiler.generateType(this);
    }

    public boolean is(String type) {
        return "functional".equals(type);
    }
}
