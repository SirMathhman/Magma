package magma.simple;

import magma.CodeEmitter;
import magma.ir.IrNode;

/** Very small emitter used by CLI for the stub pipeline. */
public class SimpleCodeEmitter implements CodeEmitter {
    @Override
    public void emit(IrNode ir) {
        // no-op for now; CLI will use the IR to decide exit code.
    }
}
