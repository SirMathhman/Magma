package magma.api.io.path;

import magma.api.collect.stream.StreamLike;
import magma.api.io.IOError;
import magma.api.optional.OptionalLike;
import magma.api.result.Result;

public interface PathLike {
    Result<String, IOError> readString();

    Result<StreamLike<PathLike>, IOError> walk();

    OptionalLike<IOError> writeString(String output);

    PathLike getFileName();

    String asString();

    boolean isRegularFile();
}
