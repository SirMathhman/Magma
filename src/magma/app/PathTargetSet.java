package magma.app;

import magma.app.compile.CompileResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public record PathTargetSet(Path root) implements TargetSet {
    @Override
    public Optional<ApplicationException> writeValue(Unit unit, CompileResult value) {
        try {
            var namespace = unit.computeNamespace().toList();
            var name = unit.computeName();

            var rootDirectory = root();
            for (String segment : namespace) {
                rootDirectory = rootDirectory.resolve(segment);
            }

            var target = rootDirectory.resolve(name + Application.EXTENSION_SEPARATOR + "mgs");
            Files.writeString(target, value.output());

            var input = rootDirectory.resolve(name + Application.EXTENSION_SEPARATOR + "input.xml");
            Files.writeString(input, value.inputNode().toXML().format(0));

            var output = rootDirectory.resolve(name + Application.EXTENSION_SEPARATOR + "output.xml");
            Files.writeString(output, value.outputNode().toXML().format(0));
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ApplicationException(e));
        }
    }
}