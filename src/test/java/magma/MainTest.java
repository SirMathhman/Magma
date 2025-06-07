package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import magma.path.NioPath;
import magma.path.PathLike;

import magma.Main;
import org.junit.jupiter.api.Test;

class MainTest {
    public static final Path ROOT = Paths.get("src/main/java/temp");

    @Test
    void buildsFilesUnderSourceDirectory() throws IOException {
        var javaDir = ROOT;
        Files.createDirectories(javaDir);
        var javaFile = javaDir.resolve("A.java");
        Files.writeString(javaFile, "package temp; public class A {}");

        Main.main(new String[0]);

        var tsFile = Paths.get("src/main/node/temp/A.ts");
        var ts = Files.readString(tsFile);
        assertEquals("export default class A {}" + System.lineSeparator(), ts);

        deleteTree(Paths.get("src/main/node"));
        deleteTree(ROOT);
    }

    private static void deleteTree(Path root) throws IOException {
        var start = NioPath.wrap(root);
        var result = start.walk();
        if (!result.isOk()) return;
        List<PathLike> paths = new ArrayList<>(result.value().get());
        for (var i = paths.size() - 1; i >= 0; i--) {
            Files.deleteIfExists(((NioPath) paths.get(i)).toNio());
        }
    }
}
