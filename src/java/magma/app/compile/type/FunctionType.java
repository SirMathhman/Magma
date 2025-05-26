package magma.app.compile.type;

import magma.api.Tuple2;
import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;

public record FunctionType(Iterable<String> args, String returns) implements Type {
    @Override
    public String generate() {
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

    @Override
    public String generateBeforeName() {
        return "";
    }

    public String generateSimple() {
        return this.generate();
    }

    public boolean is(String type) {
        return "functional".equals(type);
    }
}
