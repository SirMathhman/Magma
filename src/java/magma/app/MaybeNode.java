package magma.app;

public interface MaybeNode {
    MaybeNode withString(String key, String value);

    Generated generate(Generator generator);
}
