package magma;

public class Application {
    public void alwaysThrows() throws ApplicationException {
        throw new ApplicationException("This always throws an error.");
    }
}
