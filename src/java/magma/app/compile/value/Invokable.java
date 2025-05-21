package magma.app.compile.value;

import magma.api.collect.list.Iterable;

public record Invokable(Caller caller, Iterable<Value> args) implements Value {

}
