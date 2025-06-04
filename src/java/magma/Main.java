package magma;

import magma.PathLike;

public class Main {
    public static void main(String[] args) {
        PathLike javaRoot = PathLike.of("src/java");
        PathLike tsRoot = PathLike.of("src/node");
        TypeScriptStubs.write(javaRoot, tsRoot).ifPresent(e -> {
            e.printStackTrace();
            System.exit(1);
        });
        GenerateDiagram.writeDiagram(PathLike.of("diagram.puml")).ifPresent(e -> {
            e.printStackTrace();
            System.exit(1);
        });
    }
}
