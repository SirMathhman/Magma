package magma;

import java.util.List;
import java.util.Optional;

public record StructureHeader(String type, List<String> annotations, String beforeKeyword, String name,
                              Optional<String> maybeAfterImplements) implements StructureDefinition {
    @Override
    public String generate() {
        final var generated = this.maybeAfterImplements()
                                  .map(afterImplements -> Placeholder.generate("implements " + afterImplements))
                                  .orElse("");

        return this.type + " " + this.name() + generated;
    }
}