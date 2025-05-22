package magma.app.compile;

import magma.api.collect.Iter;
import magma.api.option.Option;
import magma.app.Location;
import magma.app.Platform;
import magma.app.io.Source;

public interface Context {
    Iter<Source> iterSources();

    boolean hasPlatform(Platform platform);

    Option<Source> findSource(String name);

    Context withLocation(Location location);

    Context addSource(Source source);

    Option<Location> findLocation();

    Context withPlatform(Platform platform);
}
