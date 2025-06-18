package magma.app;

import magma.app.jvm.JVMPath;

import java.io.IOException;
import java.util.Set;

public interface PathLike {
    String readString() throws IOException;

    void writeString(CharSequence content) throws IOException;

    boolean isRegularFile();

    Set<JVMPath> walk() throws IOException;

    String getFileNameAsString();

    String asString();
}
