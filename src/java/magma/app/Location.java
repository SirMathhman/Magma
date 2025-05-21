package magma.app;

import magma.api.collect.Iter;
import magma.api.collect.list.List;

public record Location(List<String> namespace, String name) {
    public String attachExtension(String extension) {
        return this.name + "." + extension;
    }

    Iter<String> iterNamespace() {
        return this.namespace.iter();
    }
}
