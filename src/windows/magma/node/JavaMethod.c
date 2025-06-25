#include "JavaMethod.h"
#include "../CFunction.h"
#include "../Strings.h"
/*

public record JavaMethod(JavaHeader header, String compiledParams, String compiledContent) implements JavaClassSegment {
    public CFunction toCFunction(final String structureName) {
        return switch (this.header) {
            case final Constructor constructor -> {
                final String outputContent = Strings.LINE_SEPARATOR + "\tstruct " + constructor.name() + " this;" + this.compiledContent + Strings.LINE_SEPARATOR + "\treturn this;";
                yield new CFunction(new CDefinition(constructor.beforeName(),
                        new Struct(constructor.name()),
                        "new_" + constructor.name()), this.compiledParams, outputContent);
            }
            case final JavaDefinition definition -> {
                final CHeader newHeader = definition.toCDefinition("_" + structureName);
                yield new CFunction(newHeader, this.compiledParams, this.compiledContent);
            }
            case final Placeholder placeholder -> {
                yield new CFunction(placeholder, this.compiledParams, this.compiledContent);
            }
        };
    }
}*//**/