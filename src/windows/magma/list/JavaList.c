#include "JavaList.h"
#include "../../java/util/ArrayList.h"
#include "../../java/util/Iterator.h"
#include "../../java/util/List.h"
#include "../../java/util/stream/Stream.h"
/*

public record JavaList<Value>(List<Value> list) implements ListLike<Value> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public ListLike<Value> add(final Value element) {
        this.list.add(element);
        return this;
    }

    @Override
    public Stream<Value> stream() {
        return this.list.stream();
    }

    @Override
    public Iterator<Value> iterator() {
        return this.list.iterator();
    }
}*//*
*/