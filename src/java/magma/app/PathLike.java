package magma.app;

import java.io.IOException;
import java.util.Set;

public interface PathLike {
    String readString() throws IOException;

    void writeString(CharSequence content) throws IOException;

    boolean isRegularFile();

    Set<PathLike> walk() throws IOException;

    String getFileNameAsString();

    String asString();
}
