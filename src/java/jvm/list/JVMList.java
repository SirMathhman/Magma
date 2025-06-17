package jvm.list;

import magma.api.list.ListLike;

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
}
