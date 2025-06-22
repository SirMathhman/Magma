package magma.string;

import magma.node.result.Matching;

public sealed interface StringResult extends Appending<StringResult>, Matching<String> permits StringOk, StringErr {
    StringResult prepend(String slice);
}
