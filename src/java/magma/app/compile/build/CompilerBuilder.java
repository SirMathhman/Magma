package magma.app.compile.build;

import magma.app.compile.Compiler;
import magma.app.compile.StageCompiler;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.JavaPlantTransformer;
import magma.app.compile.lang.PlantLang;

public class CompilerBuilder {
    public static Compiler createCompiler() {
        final var sourceRule = JavaLang.createJavaRootRule();
        final var transformer = new JavaPlantTransformer();
        final var targetRule = PlantLang.createPlantRootRule();
        return new StageCompiler(sourceRule, transformer, targetRule);
    }
}