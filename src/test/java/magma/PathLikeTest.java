package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.path.NioPath;
import magma.path.PathLike;
import org.junit.jupiter.api.Test;

class PathLikeTest {
    @Test
    void resolvesAndRelativizesPaths() {
        PathLike base = NioPath.of("a");
        PathLike child = base.resolve("b");
        assertEquals("a/b", child.toString());
        PathLike rel = base.relativize(child);
        assertEquals("b", rel.toString());
        assertEquals("a", child.getParent().toString());
    }
}
