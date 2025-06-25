#include "MutableState.h"
/*import magma.list.ListLike;*/
/*import magma.list.Lists;*/
/*public */struct MutableState {
	/*private */struct ListLike_char_ptr segments;
	/*private */char* buffer;
	/*private */int depth;/*
*/};
/*private */struct MutableState new_MutableState(/*final */struct ListLike_char_ptr segments) {
	this.segments = segments;
	this.buffer = "";
	this.depth = 0;
}
/*public */struct MutableState new_MutableState() {
	/*this(Lists.empty())*/;
}
/*@Override
    public State */struct append new_append(/*final *//*char*/ c) {
	this.buffer = this.buffer + c;
	/*return this*/;
}
/*@Override
    public State */struct advance new_advance() {
	this.segments = this.segments.add(this.buffer);
	this.buffer = "";
	/*return this*/;
}
/*@Override
    public ListLike<String> */struct unwrap new_unwrap() {
	/*return this.segments*/;
}
/*@Override
    public boolean */struct isLevel new_isLevel() {
	/*return 0 */ = /*= this*/.depth;
}
/*@Override
    public State */struct enter new_enter() {
	/*this.depth++*/;
	/*return this*/;
}
/*@Override
    public State */struct exit new_exit() {
	/*this.depth--*/;
	/*return this*/;
}
/*@Override
    public boolean */struct isShallow new_isShallow() {
	/*return 1 */ = /*= this*/.depth;
}
/*
*/