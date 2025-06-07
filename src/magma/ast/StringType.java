package magma.ast;

import magma.util.*;
import magma.compile.*;
public class StringType implements Type {
    @Override
    public String generate() {
        return "string";
    }
}
