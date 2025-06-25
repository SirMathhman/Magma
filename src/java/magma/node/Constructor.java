package magma.node;

public record Constructor(String beforeName, String name) implements JavaHeader {
    @Override
    public boolean isNamed(final String name) {
        return "new".equals(name);
    }
}