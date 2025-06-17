package magma.api.io;

import magma.api.list.ListLike;
import magma.api.result.Result;

public interface PathLike {
    IOOption writeString(CharSequence output);

    Result<ListLike<PathLike>, IOError> walk();

    Result<String, IOError> readString();

    int getNameCount();

    PathLike getName(int index);

    String asString();

    PathLike relativize(PathLike child);

    PathLike getParent();

    PathLike getFileName();

    boolean isRegularFile();
}
