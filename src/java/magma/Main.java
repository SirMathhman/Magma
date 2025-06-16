package magma;

import magma.app.Application;
import magma.app.compile.RuleCompiler;
import magma.app.compile.lang.CommonLang;

@SuppressWarnings("ClassWithTooManyTransitiveDependencies")
public class Main {
    public static void main(String[] args) {
        new Application(new RuleCompiler(CommonLang.createJavaRootRule(), CommonLang.createPlantUMLRootRule())).run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}
