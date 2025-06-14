package magma.app.compile;

public interface Rule<N, L, G> {
    L lex(String input);

    G generate(N node);
}