package magma.api.error.list;

public interface ErrorList<Error> extends ErrorSequence<Error> {
    ErrorList<Error> add(Error error);
}
