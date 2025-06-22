package magma.path;

import magma.error.IOError;
import magma.result.Result;

import java.util.List;
import java.util.Optional;

public interface PathLike {
    Result<String> readString();

    Result<List<PathLike>> walk();

    Optional<IOError> writeString(CharSequence output);

    PathLike getFileName();

    String asString();
}
