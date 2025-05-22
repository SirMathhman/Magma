package magma.app.compile.compose;

import magma.api.Tuple2;
import magma.api.option.Option;
import magma.app.compile.locate.FirstLocator;
import magma.app.compile.locate.LastLocator;
import magma.app.compile.split.LocatingSplitter;
import magma.app.compile.split.Splitter;

import java.util.function.BiFunction;

public record Split<T>(Splitter splitter, Composable<Tuple2<String, String>, T> mapper) implements Composable<String, T> {
    public static <T> Composable<String, T> last(String infix, BiFunction<String, String, Option<T>> mapper) {
        return new Split<T>(new LocatingSplitter(infix, new LastLocator()), Composable.toComposable(mapper));
    }

    public static <T> Composable<String, T> first(String infix, BiFunction<String, String, Option<T>> mapper) {
        return new Split<T>(new LocatingSplitter(infix, new FirstLocator()), Composable.toComposable(mapper));
    }

    @Override
    public Option<T> apply(String input) {
        return this.splitter().apply(input).flatMap(this.mapper::apply);
    }
}