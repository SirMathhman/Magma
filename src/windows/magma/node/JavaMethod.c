#include "JavaMethod.h"
#include "../CFunction.h"
#include "../Strings.h"
#include "../../java/util/List.h"
/*

public record JavaMethod(JavaHeader header, List<JavaParameter> parameters, String compiledContent) implements
        JavaClassSegment {
    public CFunction toCFunction(final String structureName) {
        final var newParameters = this.parameters.stream()
                .map(JavaParameter::toCParameter)
                .toList();

        return switch (this.header) {
            case final Constructor constructor -> {
                final String outputContent = Strings.LINE_SEPARATOR + "\tstruct " + constructor.name() + " this;" + this.compiledContent + Strings.LINE_SEPARATOR + "\treturn this;";
                yield new CFunction(new CDefinition(constructor.beforeName(),
                        new Struct(constructor.name()), "new_" + constructor.name()), newParameters, outputContent);
            }
            case final JavaDefinition definition -> {
                final CHeader newHeader = definition.toCDefinition("_" + structureName);
                yield new CFunction(newHeader, newParameters, this.compiledContent);
            }
            case final Placeholder placeholder -> {
                yield new CFunction(placeholder, newParameters, this.compiledContent);
            }
        };
    }

    public boolean isNamed(final String name) {
        return this.header.isNamed(name);
    }
}*//**/