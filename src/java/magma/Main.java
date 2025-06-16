package magma;

import magma.app.Application;
import magma.app.compile.RuleCompiler;
import magma.app.compile.error.DefaultCompileResultFactory;
import magma.app.compile.lang.JavaRootRuleFactory;
import magma.app.compile.lang.PlantUMLRootRuleFactory;
import magma.app.compile.lang.build.FactoryRuleBuilder;
import magma.app.compile.node.MapNodeFactory;

public class Main {
    public static void main(String[] args) {
        var builder = new FactoryRuleBuilder<>(new MapNodeFactory(), DefaultCompileResultFactory.create());
        new Application(new RuleCompiler(new JavaRootRuleFactory<>(builder).create(), new PlantUMLRootRuleFactory<>(builder).create())).run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}
