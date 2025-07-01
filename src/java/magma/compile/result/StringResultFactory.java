package magma.compile.result;

import java.util.List;

public interface StringResultFactory<Node, Error, StringResult> {
    StringResult createStringError(String message, Node node);

    StringResult createStringErrorWithChildren(String message, Node context, List<Error> errors);

    StringResult createString(String value);

    StringResult createEmptyString();
}
