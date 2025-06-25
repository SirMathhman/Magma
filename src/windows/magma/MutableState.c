#include "MutableState.h"
/*public */struct MutableState {
	/*private*/ /*ListLike<String>*/ segments;
	/*private*/ /*String*/ buffer;
	/*private*/ /*int*/ depth;/*

    private MutableState(final ListLike<String> segments, final String buffer, final int depth) {
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;
    }

    MutableState() {
        this(Lists.empty(), "", 0);
    }

    @Override
    public State append(final char c) {
        buffer = buffer + c;
        return this;
    }

    @Override
    public State advance() {
        segments = segments.add(buffer);
        buffer = "";
        return this;
    }

    @Override
    public ListLike<String> unwrap() {
        return segments;
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
*/};
