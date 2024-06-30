package magma.build.compile.parse.rule;

public interface Filter {
    String computeMessage();

    boolean filter(String input);
}
