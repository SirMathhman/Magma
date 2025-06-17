package magma.api.io;

import magma.api.list.Sequence;
import magma.api.result.Result;

public interface PathLike {
    IOOption writeString(CharSequence output);

    Result<Sequence<PathLike>, IOError> walk();

    Result<String, IOError> readString();

    int getNameCount();

    PathLike getName(int index);

    String asString();

    PathLike relativize(PathLike child);

    PathLike getParent();

    PathLike getFileName();

    boolean isRegularFile();
}
