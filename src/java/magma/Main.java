package magma;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path javaRoot = Path.of("src/java");
        Path tsRoot = Path.of("src/node");
        TypeScriptStubs.write(javaRoot, tsRoot).ifPresent(e -> {
            e.printStackTrace();
            System.exit(1);
        });
        GenerateDiagram.writeDiagram(Path.of("diagram.puml")).ifPresent(e -> {
            e.printStackTrace();
            System.exit(1);
        });
    }
}
