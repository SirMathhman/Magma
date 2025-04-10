package magma;

import java.nio.file.Path;

public class Paths {
    private record NIOPath(Path path) implements Main.Path_ {
        @Override
        public Main.Path_ resolveSibling(String sibling) {
            return new NIOPath(this.path.resolveSibling(sibling));
        }

        @Override
        public Main.List_<String> asList() {
            return new Main.HeadedIterator<>(new Main.RangeHead(this.path.getNameCount()))
                    .map(index -> this.path.getName(index).toString())
                    .collect(new Main.ListCollector<>());
        }

        @Override
        public Main.Path_ resolveChild(String child) {
            return new NIOPath(this.path.resolve(child));
        }
    }

    public static Main.Path_ get(String first, String... elements) {
        return new NIOPath(java.nio.file.Paths.get(first, elements));
    }
}
