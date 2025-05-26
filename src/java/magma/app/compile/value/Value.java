package magma.app.compile.value;

import magma.api.option.Option;

public interface Value extends Argument, Caller {
    Option<String> generateAsEnumValue(String structureName);
}