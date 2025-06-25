#include "MutableState.h"
#include "../list/ListLike.h"
#include "../list/Lists.h"
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
/*public static */struct State empty_MutableState() {
	return new_MutableState(Lists.empty());
}
/*@Override
    public */struct State append_MutableState(/*final */struct char c) {
	this.buffer = /* this.buffer + c*/;
	return this;
}
/*@Override
    public */struct State advance_MutableState() {
	this.segments = this.segments.add(this.buffer);
	this.buffer = "";
	return this;
}
/*@Override
    public */struct ListLike_char_ptr unwrap_MutableState() {
	return this.segments;
}
/*@Override
    public */struct boolean isLevel_MutableState() {
	return /*0 == this*/.depth;
}
/*@Override
    public */struct State enter_MutableState() {
	/*this.depth++*/;
	return this;
}
/*@Override
    public */struct State exit_MutableState() {
	/*this.depth--*/;
	return this;
}
/*@Override
    public */struct boolean isShallow_MutableState() {
	return /*1 == this*/.depth;
}
/*
*/