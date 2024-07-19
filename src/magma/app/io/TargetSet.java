package magma.app.io;

import magma.app.ApplicationException;

public interface TargetSet {
    void write(Unit unit, String output) throws ApplicationException;
}
