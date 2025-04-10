package magma;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Paths {
    private record NIOPath(Path path) implements Main.Path_ {
        @Override
        public Main.Path_ resolveSibling(String sibling) {
            return new NIOPath(this.path.resolveSibling(sibling));
        }

        @Override
        public List<String> asList() {
            ArrayList<String> segments = new ArrayList<>();
            for (int i = 0; i < this.path.getNameCount(); i++) {
                segments.add(this.path.getName(i).toString());
            }
            return segments;
        }

    }

    public static Main.Path_ get(String first, String... elements) {
        return new NIOPath(java.nio.file.Paths.get(first, elements));
    }
}
