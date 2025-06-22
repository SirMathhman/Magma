package magma.api.io;

import magma.api.list.ListLike;
import magma.api.option.Option;
import magma.api.result.Result;

public interface PathLike {
    Result<String, IOError> readString();

    Result<ListLike<PathLike>, IOError> walk();

    Option<IOError> writeString(CharSequence output);

    PathLike getFileName();

    String asString();
}
