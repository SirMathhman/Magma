package magma.app.io.source;

import jvm.list.JVMLists;
import magma.api.io.IOError;
import magma.api.io.PathLike;
import magma.api.list.ListLike;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Result;
import magma.app.io.location.Location;
import magma.app.io.location.SimpleLocation;

public record PathSource(PathLike sourceDirectory, PathLike source) implements Source {
    private static ListLike<String> computeNamespace(PathLike parent) {
        final ListLike<String> segments = JVMLists.empty();
        for (var i = 0; i < parent.getNameCount(); i++)
            segments.add(parent.getName(i)
                    .asString());
        return segments;
    }

    @Override
    public Result<String, IOError> readString() {
        return this.source.readString();
    }

    @Override
    public Location computeLocation() {
        final var relative = this.sourceDirectory()
                .relativize(this.source);
        final var relativeParent = relative.getParent();

        final var segments = computeNamespace(relativeParent);
        final var namespace = this.join(".", segments);

        final var fileName = this.source.getFileName()
                .asString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        return new SimpleLocation(namespace, name);
    }

    private String join(String delimeter, ListLike<String> list) {
        Option<String> option = new None<String>();
        for (var i = 0; i < list.size(); i++) {
            final var element = list.get(i);
            if (option.isPresent())
                option = new Some<>(option.get() + delimeter + element);
            else
                option = new Some<>(element);
        }

        return option.orElse("");
    }
}