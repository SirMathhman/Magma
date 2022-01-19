package com.meti.app.compile.stage;

import com.meti.api.collect.java.List;
import com.meti.api.collect.stream.Stream;
import com.meti.api.collect.stream.StreamException;
import com.meti.app.compile.node.Node;
import com.meti.app.source.Packaging;

public class CMagmaPipeline {
    private final CMagmaNodeResolver resolver = new CMagmaNodeResolver();
    private final CMagmaModifier modifier = new CMagmaModifier();
    private final CFormatter formatter;
    private final List<Node> input;
    private final CFlattener flattener = new CFlattener();

    public CMagmaPipeline(Packaging thisPackage, List<Node> input) {
        this.formatter = new CFormatter(thisPackage);
        this.input = input;
    }

    public Stream<Node> apply() throws CompileException {
        try {
            var resolved = perform(resolver, input);
            var formatted = perform(formatter, resolved);
            var modified = perform(modifier, formatted);
            return perform(flattener, modified).stream();
        } catch (StreamException e) {
            throw new CompileException(e);
        }
    }

    private List<Node> perform(AbstractStage stage, List<Node> current) throws StreamException {
        return current.stream()
                .map(stage::transformNodeAST)
                .foldRight(List.createList(), List::add);
    }
}
