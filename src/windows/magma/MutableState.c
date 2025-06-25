#include "MutableState.h"
/*public */struct MutableState {
	/*private */struct ListLike_char_ptr segments;
	/*private */char* buffer;
	/*private */int depth;/*

    MutableState() {
        this(Lists.empty(), "", 0);
    }*//*
*/};
/*private*/ new_MutableState(/*final ListLike<String> segments, final String buffer, final int depth*/) {/*
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;
    */}
/*@Override
    public State*/ new_append(/*final char c*/) {/*
        buffer = buffer + c;
        return this;
    */}
/*@Override
    public State*/ new_advance(/**/) {/*
        segments = segments.add(buffer);
        buffer = "";
        return this;
    */}
/*@Override
    public ListLike<String>*/ new_unwrap(/**/) {/*
        return segments;
    */}
/*@Override
    public boolean*/ new_isLevel(/**/) {/*
        return 0 == depth;
    */}
/*@Override
    public State*/ new_enter(/**/) {/*
        depth++;
        return this;
    */}
/*@Override
    public State*/ new_exit(/**/) {/*
        depth--;
        return this;
    */}
/*@Override
    public boolean*/ new_isShallow(/**/) {/*
        return 1 == depth;
    */}
/*
*/