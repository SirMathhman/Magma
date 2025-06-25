#include "JavaDefinition.h"
/*

public record JavaDefinition(String beforeType, JavaType type, String name) implements JavaHeader, JavaClassSegment, JavaParameter {
    public CDefinition toCDefinition() {
        return this.toCDefinition("");
    }

    public CDefinition toCDefinition(final String suffix) {
        return new CDefinition(this.beforeType, this.type.toCType(), this.name + suffix);
    }

    @Override
    public boolean isNamed(final String name) {
        return this.name.equals(name);
    }

    @Override
    public CParameter toCParameter() {
        return toCDefinition();
    }
}*//**/