#include "CDefinition.h"
#include "../../java/util/function/Function.h"
/*

public record CDefinition(String beforeType, CType type, String name) implements Header {
    @Override
    public String generate() {
        return Placeholder.generate(this.beforeType() + " ") + this.type()
                .generate() + " " + this.name();
    }

    public CDefinition mapName(final Function<String, String> mapper) {
        return new CDefinition(this.beforeType, this.type, mapper.apply(this.name));
    }
}*//**/