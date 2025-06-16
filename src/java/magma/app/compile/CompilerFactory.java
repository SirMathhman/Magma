package magma.app.compile;

import magma.app.compile.error.DefaultCompileResultFactory;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.StringResult;
import magma.app.compile.lang.JavaRootRuleFactory;
import magma.app.compile.lang.PlantUMLRootRuleFactory;
import magma.app.compile.lang.build.FactoryRuleBuilder;
import magma.app.compile.node.MapNodeFactory;

public class CompilerFactory {
    public static Compiler<StringResult<FormattedError>> create() {
        var builder = new FactoryRuleBuilder<>(new MapNodeFactory(), DefaultCompileResultFactory.create());
        return new RuleCompiler(new JavaRootRuleFactory<>(builder).create(), new PlantUMLRootRuleFactory<>(builder).create());
    }
}
