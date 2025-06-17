package magma.app.io.source;

import jvm.list.JVMLists;
import magma.api.io.IOError;
import magma.api.io.PathLike;
import magma.api.list.Sequence;
import magma.api.result.Result;

public record PathSources(PathLike root) implements Sources {
    @Override
    public Result<Sequence<Source>, IOError> collect() {
        return this.root.walk()
                .map(this::filter);
    }

    private Sequence<Source> filter(Sequence<PathLike> sources) {
        final var set = JVMLists.<Source>empty();
        for (var i = 0; i < sources.size(); i++) {
            final var source = sources.get(i);

            if (source.isRegularFile() && source.asString()
                    .endsWith(".java"))
                set.add(new PathSource(this.root, source));
        }

        return set;
    }
}