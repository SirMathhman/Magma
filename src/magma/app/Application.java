package magma.app;

import magma.api.Result;
import magma.app.compile.Compiler;
import magma.app.compile.lang.java.JavaLang;
import magma.app.compile.lang.magma.MagmaLang;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

public final class Application {
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");
    private final SourceSet sourceSet;
    private final TargetSet targetSet;

    public Application(SourceSet sourceSet, TargetSet targetSet) {
        this.sourceSet = sourceSet;
        this.targetSet = targetSet;
    }

    public Optional<ApplicationException> run() {
        return sourceSet.collectSources().match(this::onCollect, err -> Optional.of(new ApplicationException(err)));
    }

    private Optional<ApplicationException> onCollect(Set<Unit> set) {
        return set.stream().reduce(Optional.empty(),
                (previousError, unit) -> previousError.or(() -> compileUnit(unit)),
                (previousError, e2) -> previousError.or(() -> e2));
    }

    private Optional<ApplicationException> compileUnit(Unit unit) {
        return readInput(unit).match(input -> compileWithInput(unit, input), Optional::of);
    }

    private Optional<ApplicationException> compileWithInput(Unit unit, String input) {
        System.out.println(unit);
        return new Compiler(JavaLang.createRootJavaRule(), MagmaLang.createRootMagmaRule())
                .compile(unit, input)
                .mapValue(value1 -> targetSet.writeValue(unit, value1))
                .match(value -> value, Optional::of);
    }

    private Result<String, ApplicationException> readInput(Unit unit) {
        return unit.read().mapErr(ApplicationException::new);
    }
}