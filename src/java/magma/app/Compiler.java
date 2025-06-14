package magma.app;

import magma.app.compile.Lang;
import magma.app.compile.Transformer;

public class Compiler {
    public static String compile(String input, String name) {
        return Lang.createJavaRootRule().lex(input).findValue().flatMap(root -> {
            final var parsed = Transformer.transform(name, root);
            return Lang.createPlantRootRule().generate(parsed).findValue();
        }).orElse("");
    }
}