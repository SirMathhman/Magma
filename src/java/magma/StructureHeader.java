package magma;

import java.util.List;

public record StructureHeader(String type, List<String> annotations, String beforeKeyword, String name,
                              Optional<String> maybeAfterImplements) implements StructureDefinition {
    @Override
    public String generate() {
        return this.type + " " + this.name;
    }
}