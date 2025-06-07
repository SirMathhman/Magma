package magma.ast;

import magma.util.*;

public record TypeParamSet(List<TypeParam> typeParams) {
    public TypeParamSet() {
        this(Lists.empty());
    }

    public Option<TypeParam> resolve(String name) {
        return typeParams.iter()
                .filter(typeParam -> typeParam.name().equals(name))
                .next();
    }
}
