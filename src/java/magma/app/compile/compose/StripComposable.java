package magma.app.compile.compose;

import magma.api.option.Option;

public record StripComposable<T>(Composable<String, T> composable) implements Composable<String, T> {
    @Override
    public Option<T> apply(String s) {
        return this.composable.apply(s.strip());
    }
}
