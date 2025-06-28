package magma;

import java.util.function.Function;

public record Assignment(Assignable assignable, String value) {
    String generate() {
        return this.assignable().generate() + " = " + this.value();
    }

    public Assignment mapAssignable(final Function<Assignable, Assignable> mapper) {
        return new Assignment(mapper.apply(this.assignable), this.value);
    }
}