package magma.path;

import java.nio.file.Path;

public record JavaPath(Path path) implements PathLike {
    @Override
    public PathLike getFileName() {
        return new JavaPath(this.path.getFileName());
    }

    @Override
    public Path unwrap() {
        return this.path;
    }

    @Override
    public String asString() {
        return this.path.toString();
    }
}
