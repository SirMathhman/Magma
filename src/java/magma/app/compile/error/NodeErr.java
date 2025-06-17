package magma.app.compile.error;

public record NodeErr(FormattedError node) implements NodeResult {
}
