package magma.app.compile.value;

import magma.api.option.Option;

public sealed interface Value extends Caller permits AccessValue, Invokable, Lambda, Not, Operation, Placeholder, StringValue, Symbol {
    Option<String> generateAsEnumValue(String structureName);
}