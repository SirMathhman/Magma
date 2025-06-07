package magma;

interface Type extends Generating, TypeArgument {
    default Map<String, Type> extract(Type actual) {
        return Maps.empty();
    }

    default Type resolve(Map<String, Type> resolved) {
        return this;
    }
}
