#include "GenericType.h"
#include "../../java/util/Optional.h"
/*

public record GenericType(String base, JavaType type) implements JavaType {
    @Override
    public Struct toCType() {
        return new Struct(this.base + "_" + this.type.toCType()
                .generateSymbol());
    }

    @Override
    public Optional<String> findBaseName() {
        return Optional.of(this.base);
    }
}*//**/