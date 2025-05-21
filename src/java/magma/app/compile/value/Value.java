package magma.app.compile.value;

import magma.api.option.Option;

public sealed interface Value extends Argument, Caller permits Symbol, AccessValue, Invokable, Lambda, Not, Operation, Placeholder, StringValue {
    Option<String> generateAsEnumValue(String structureName);
}