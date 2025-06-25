#include "CFunction.h"
#include "node/CHeader.h"
#include "node/CParameter.h"
#include "../java/util/List.h"
#include "../java/util/stream/Collectors.h"
/*

public record CFunction(CHeader header, List<CParameter> params, String content) {
    String generate() {
        final var objectStream = this.params.stream()
                .map(CParameter::generate)
                .collect(Collectors.joining(", "));

        return this.header.generate() + "(" + objectStream + ") {" + this.content() + Strings.LINE_SEPARATOR + "}" + Strings.LINE_SEPARATOR;
    }
}*//**/