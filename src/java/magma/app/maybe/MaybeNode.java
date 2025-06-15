package magma.app.maybe;

import magma.app.Generated;
import magma.app.Generator;

public interface MaybeNode {
    MaybeNode withString(String key, String value);

    Generated generate(Generator generator);
}
