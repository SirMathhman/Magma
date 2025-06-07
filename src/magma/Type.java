package magma;

/**
 * Represents a type that can generate code and be used as a type argument.
 */
interface Type extends Generating, TypeArgument {
    default Map<String, Type> extract(Type actual) {
        return Maps.empty();
    }

    default Type resolve(Map<String, Type> resolved) {
        return this;
    }
}
