package magma;

import magma.app.Application;
import magma.app.compile.CompilerImpl;
import magma.app.compile.lang.CommonLang;

public class Main {
    public static void main(String[] args) {
        new Application(new CompilerImpl(CommonLang.createJavaRootRule(), CommonLang.createPlantUMLRootRule())).run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}
