#include "MutableState.h"
/*import java.util.ArrayList;*/
/*import java.util.Collections;*/
/*import java.util.List;*/
/*public class MutableState implements State */{};
/*
    private final List<String> segments;
    private StringBuilder buffer;
    private int depth;

    private MutableState(final List<String> segments, final StringBuilder buffer, final int depth) {
        this.segments = new ArrayList<>(segments);
        this.buffer = buffer;
        this.depth = depth;
    }

    MutableState() {
        this(new ArrayList<>(), new StringBuilder(), 0);
    }

    @Override
    public State append(final char c) {
        buffer.append(c);
        return this;
    }

    @Override
    public State advance() {
        segments.add(buffer.toString());
        buffer = new StringBuilder();
        return this;
    }

    @Override
    public List<String> unwrap() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public boolean isLevel() {
        return 0 == depth;
    }

    @Override
    public State enter() {
        depth++;
        return this;
    }

    @Override
    public State exit() {
        depth--;
        return this;
    }
*/