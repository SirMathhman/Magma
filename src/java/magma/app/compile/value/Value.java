package magma.app.compile.value;

import magma.api.option.Option;
import magma.app.compile.node.Node;

public sealed interface Value extends Node permits AccessValue, Invokable, Lambda, Not, Operation, Placeholder, StringValue, Symbol {
    Option<String> generateAsEnumValue(String structureName);
}