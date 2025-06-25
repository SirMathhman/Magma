#include "MutableState.h"
/*import magma.list.ListLike;*/
/*import magma.list.Lists;*/
/*public */struct MutableState {
	/*private */struct ListLike_char_ptr segments;
	/*private */char* buffer;
	/*private */int depth;/*
*/};
/*private */struct MutableState new_MutableState(/*final */struct ListLike_char_ptr segments) {
	struct MutableState this;
	this.segments = segments;
	this.buffer = "";
	this.depth = 0;
	return this;
}
/*public static State */struct empty new_empty() {
	struct empty this;
	/*return new MutableState(Lists.empty())*/;
	return this;
}
/*@Override
    public State */struct append new_append(/*final *//*char*/ c) {
	struct append this;
	this.buffer = this.buffer + c;
	/*return this*/;
	return this;
}
/*@Override
    public State */struct advance new_advance() {
	struct advance this;
	this.segments = this.segments.add(this.buffer);
	this.buffer = "";
	/*return this*/;
	return this;
}
/*@Override
    public ListLike<String> */struct unwrap new_unwrap() {
	struct unwrap this;
	/*return this.segments*/;
	return this;
}
/*@Override
    public boolean */struct isLevel new_isLevel() {
	struct isLevel this;
	/*return 0 */ = /*= this*/.depth;
	return this;
}
/*@Override
    public State */struct enter new_enter() {
	struct enter this;
	/*this.depth++*/;
	/*return this*/;
	return this;
}
/*@Override
    public State */struct exit new_exit() {
	struct exit this;
	/*this.depth--*/;
	/*return this*/;
	return this;
}
/*@Override
    public boolean */struct isShallow new_isShallow() {
	struct isShallow this;
	/*return 1 */ = /*= this*/.depth;
	return this;
}
/*
*/