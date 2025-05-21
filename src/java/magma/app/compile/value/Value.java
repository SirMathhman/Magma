package magma.app.compile.value;

import magma.api.option.Option;
import magma.app.ValueCompiler;

public sealed interface Value extends Argument, Caller permits Symbol, AccessValue, Invokable, Lambda, Not, Operation, Placeholder, StringValue {
    Option<String> generateAsEnumValue(String structureName);

    default String generate() {
        return ValueCompiler.generateValue(this);
    }
}