package magma.api.io;

import magma.api.option.Option;
import magma.api.result.Result;

import java.util.Set;

public interface PathLike {
    Option<IOError> writeString(CharSequence output);

    Result<Set<PathLike>, IOError> walk();

    Result<String, IOError> readString();

    int getNameCount();

    PathLike getName(int index);

    String asString();

    PathLike relativize(PathLike child);

    PathLike getParent();

    PathLike getFileName();

    boolean isRegularFile();
}
