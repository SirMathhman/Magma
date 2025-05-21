package magma.app.compile.value;

import magma.api.collect.list.Iterable;
import magma.api.option.None;
import magma.api.option.Option;
import magma.app.compile.define.Definition;

public record Lambda(Iterable<Definition> parameters, String content) implements Value {

    @Override
    public Option<String> generateAsEnumValue(String structureName) {
        return new None<String>();
    }
}
