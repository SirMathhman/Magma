package magma;

import java.nio.file.Path;

public interface PathLike {
    Result<String, IOError> readString();

    Result<StreamLike<Path>, IOError> walk();

    OptionalLike<IOError> writeString(String output);
}
