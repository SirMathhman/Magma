package magma.app.compile;

import magma.app.compile.error.FormattedError;
import magma.app.compile.error.StringResult;
import magma.app.compile.lang.JavaRootRuleFactory;
import magma.app.compile.lang.PlantUMLRootRuleFactory;
import magma.app.compile.lang.build.RuleFactories;

public class CompilerFactory {
    public static Compiler<StringResult<FormattedError>> create() {
        var builder = RuleFactories.create();
        final var java = new JavaRootRuleFactory<>(builder).create();
        final var plant = new PlantUMLRootRuleFactory<>(builder).create();
        return new RuleCompiler(java, plant);
    }
}