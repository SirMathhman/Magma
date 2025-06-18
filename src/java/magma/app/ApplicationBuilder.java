package magma.app;

import magma.api.io.JVMPaths;
import magma.app.compile.build.CompilerBuilder;

public class ApplicationBuilder {
    public static Application create() {
        final var root = JVMPaths.get(".", "src", "java");
        final var target = JVMPaths.get(".", "diagram.puml");
        final var compiler = CompilerBuilder.createCompiler();
        return new CompileApplication(root, target, compiler);
    }
}
