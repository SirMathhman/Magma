package magma;

import java.util.Optional;

public record StructureHeader(String beforeKeyword, String name, Optional<String> maybeAfterImplements)
        implements StructureDefinition {
    @Override
    public String generate() {
        final var generated = this.maybeAfterImplements()
                                  .map(afterImplements -> Placeholder.generate("implements " + afterImplements))
                                  .orElse("");

        return Placeholder.generate(this.beforeKeyword()) + "class " + this.name() + generated;
    }
}