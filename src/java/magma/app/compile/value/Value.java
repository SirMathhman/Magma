package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;

public sealed interface Value extends Argument, Caller permits AccessValue, Invokable, Lambda, Not, Operation, Placeholder, StringValue, Symbol {
    default Option<String> generateAsEnumValue(String structureName) {
        return new None<>();
    }
}