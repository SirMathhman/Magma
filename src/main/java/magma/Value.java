package magma;

public sealed interface Value permits IntVal, BoolVal, InstanceVal, FunctionVal, StructDefVal, EnumDefVal, EnumVariantVal, TraitDefVal, TypeAliasVal {
}
