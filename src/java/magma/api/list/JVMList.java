package magma.api.list;

import java.util.ArrayList;
import java.util.List;

public record JVMList<T>(List<T> list) implements ListLike<T> {
    public JVMList() {
        this(new ArrayList<>());
    }

    @Override
    public ListLike<T> add(T element) {
        this.list.add(element);
        return this;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public T get(int index) {
        return this.list.get(index);
    }

    @Override
    public ListLike<T> addAll(ListLike<T> others) {
        ListLike<T> current = this;
        for (var i = 0; i < others.size(); i++) {
            final var other = others.get(i);
            current = current.add(other);
        }
        return current;
    }
}
