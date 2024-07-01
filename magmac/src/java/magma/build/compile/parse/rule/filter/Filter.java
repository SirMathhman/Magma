package magma.build.compile.parse.rule.filter;

public interface Filter {
    String computeMessage();

    boolean filter(String input);
}
