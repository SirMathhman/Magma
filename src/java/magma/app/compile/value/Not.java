package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;

public record Not(String child) implements Value {

    @Override
    public Option<String> generateAsEnumValue(String structureName) {
        return new None<String>();
    }
}
