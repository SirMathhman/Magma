package magma;

public class Main {
    public static void main(String[] args) {
        PathLike javaRoot = JVMPath.of("src/java");
        PathLike tsRoot = JVMPath.of("src/node");
        TypeScriptStubs.write(javaRoot, tsRoot).ifPresent(e -> {
            e.printStackTrace();
            System.exit(1);
        });
        GenerateDiagram.writeDiagram(JVMPath.of("diagram.puml")).ifPresent(e -> {
            e.printStackTrace();
            System.exit(1);
        });
    }
}
