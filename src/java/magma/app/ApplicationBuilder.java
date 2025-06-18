package magma.app;

import magma.api.io.JVMPaths;
import magma.app.compile.Compiler;
import magma.app.compile.StageCompiler;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.PlantLang;
import magma.app.compile.transform.JavaPlantTransformer;

public class ApplicationBuilder {
    public static Application create() {
        final var root = JVMPaths.get(".", "src", "java");
        final var target = JVMPaths.get(".", "diagram.puml");
        final var compiler = createCompiler();
        return new CompileApplication(root, target, compiler);
    }

    private static Compiler createCompiler() {
        final var sourceRule = JavaLang.createJavaRootRule();
        final var transformer = new JavaPlantTransformer();
        final var targetRule = PlantLang.createPlantRootRule();
        return new StageCompiler(sourceRule, transformer, targetRule);
    }
}
