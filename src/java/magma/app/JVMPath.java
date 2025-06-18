package magma.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public record JVMPath(Path path) implements PathLike {
    @Override
    public String getFileNameAsString() {
        return new JVMPath(this.path.getFileName()).asString();
    }

    @Override
    public String readString() throws IOException {
        return Files.readString(this.path);
    }

    @Override
    public void writeString(CharSequence content) throws IOException {
        Files.writeString(this.path, content);
    }

    @Override
    public boolean isRegularFile() {
        return Files.isRegularFile(this.path);
    }

    @Override
    public Set<JVMPath> walk() throws IOException {
        try (var stream = Files.walk(this.path)) {
            return stream.map(JVMPath::new)
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public String asString() {
        return this.path.toString();
    }
}
