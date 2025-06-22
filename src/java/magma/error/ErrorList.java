package magma.error;

public interface ErrorList<Error> extends ErrorSequence<Error> {
    ErrorList<Error> add(Error error);
}
