package magma;

import magma.result.Result;

public interface PathLike {
    Result<String, IOError> readString();

    Result<StreamLike<PathLike>, IOError> walk();

    OptionalLike<IOError> writeString(String output);

    PathLike getFileName();

    String asString();

    boolean isRegularFile();
}
