package magma.app.compile.node;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Node {
    <Return> Return findStringOrElse(String key, Function<String, Return> ifPresent, Supplier<Return> ifMissing);

    Node withString(String key, String value);

    String asString();
}
