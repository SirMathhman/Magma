package magma.path;

import magma.error.IOError;
import magma.list.ListLike;
import magma.option.Option;
import magma.result.Result;

public interface PathLike {
    Result<String> readString();

    Result<ListLike<PathLike>> walk();

    Option<IOError> writeString(CharSequence output);

    PathLike getFileName();

    String asString();
}
