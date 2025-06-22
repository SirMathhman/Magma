package magma.path;

import magma.error.IOError;
import magma.list.ListLike;
import magma.result.Result;

import java.util.Optional;

public interface PathLike {
    Result<String> readString();

    Result<ListLike<PathLike>> walk();

    Optional<IOError> writeString(CharSequence output);

    PathLike getFileName();

    String asString();
}
