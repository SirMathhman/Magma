package magma.app.compile.type;

public interface Type {
    boolean isFunctional();

    boolean isVar();

    String generateBeforeName();

    String generateSimple();
}
