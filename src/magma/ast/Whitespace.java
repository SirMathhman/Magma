package magma.ast;

import magma.util.*;
import magma.compile.*;
public class Whitespace implements Parameter, Generating, ValueArgument, TypeArgument {
    @Override
    public String generate() {
        return "";
    }
}
