// Generated transpiled C++ from 'src\main\java\magma\list\HeadedStream.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct HeadedStream {};
struct FlatMapHead {};
public FlatMapHead_FlatMapHead() {
	this.mapper=mapper;
	currentInnerHead=new_???();
}
Option<R> next_FlatMapHead() {
	while (true)
	{
	if (/*???*/)
	{
	Option<R> innerNext=innerHead.next();
	if (/*???*/)return innerNext;
	currentInnerHead=new_???();}
	Option<T> outerNext=head.next();
	if (/*???*/)
	{
	Stream<R> innerStream=mapper.apply();
	if (/*???*/)currentInnerHead=new_???();
	else
	return new_???();}
	else
	return new_???();}
}
Stream<R> map_HeadedStream() {
	return new_???();
}
R fold_HeadedStream() {
	R current=initial;
	while (true)
	{
	R finalCurrent=current;
	Option<R> map=head.next().map();
	if (/*???*/)current=value;
	else
	return current;}
}
R collect_HeadedStream() {
	return fold();
}
void forEach_HeadedStream() {
	while (true)
	{
	Option<T> next=head.next();
	if (/*???*/)consumer.accept();
	else
	break}
}
Stream<R> flatMap_HeadedStream() {
	return new_???();
}
Stream<T> filter_HeadedStream() {
	return new_???();
}
boolean allMatch_HeadedStream() {
	return fold();
}
boolean anyMatch_HeadedStream() {
	return fold();
}
