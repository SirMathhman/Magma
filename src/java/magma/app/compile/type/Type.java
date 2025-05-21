package magma.app.compile.type;

public sealed interface Type permits FunctionType, Placeholder, PrimitiveType, Symbol, TemplateType, VariadicType {
    boolean isFunctional();

    boolean isVar();

    String generateBeforeName();

    String generateSimple();
}
