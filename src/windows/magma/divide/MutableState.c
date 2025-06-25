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
/*public static *//*State*/ empty() {
	/*return new MutableState(Lists.empty())*/;
}
/*@Override
    public *//*State*/ append(/*final *//*char*/ c) {
	this.buffer = this.buffer + c;
	/*return this*/;
}
/*@Override
    public *//*State*/ advance() {
	this.segments = this.segments.add(this.buffer);
	this.buffer = "";
	/*return this*/;
}
/*@Override
    public */struct ListLike_char_ptr unwrap() {
	/*return this.segments*/;
}
/*@Override
    public *//*boolean*/ isLevel() {
	/*return 0 */ = /*= this*/.depth;
}
/*@Override
    public *//*State*/ enter() {
	/*this.depth++*/;
	/*return this*/;
}
/*@Override
    public *//*State*/ exit() {
	/*this.depth--*/;
	/*return this*/;
}
/*@Override
    public *//*boolean*/ isShallow() {
	/*return 1 */ = /*= this*/.depth;
}
/*
*/