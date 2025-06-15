package magma.app.result;

import magma.app.Result;

public class EmptyResult implements Result {
    @Override
    public StringBuilder appendTo(StringBuilder cache) {
        return cache;
    }
}
