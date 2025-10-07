// Generated transpiled C++ from 'src\main\java\magma\list\HeadedStream.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct HeadedStream {Head<T> head;};
struct FlatMapHead {Stream<R> (*)(T) mapper;Option<Head<R>> currentInnerHead;};
public FlatMapHead_FlatMapHead(Stream<R> (*mapper)(T)) {
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
	Stream<R> innerStream=mapper.apply(value);
	if (/*???*/)currentInnerHead=new_???(head1);
	else
	return new_???();}
	else
	return new_???();}
}
Stream<R> map_HeadedStream(R (*mapper)(T)) {
	return new_???(/*???*/.next().map(mapper));
}
R fold_HeadedStream(R initial, BiFunction<R, T, R> folder) {
	R current=initial;
	while (true)
	{
	R finalCurrent=current;
	Option<R> map=head.next().map(/*???*/.apply(finalCurrent, inner));
	if (/*???*/)current=value;
	else
	return current;}
}
R collect_HeadedStream(Collector<T, R> collector) {
	return fold(collector.initial(), /*???*/);
}
void forEach_HeadedStream(Consumer<T> consumer) {
	while (true)
	{
	Option<T> next=head.next();
	if (/*???*/)consumer.accept(temp);
	else
	break}
}
Stream<R> flatMap_HeadedStream(Stream<R> (*mapper)(T)) {
	return new_???(new_???(mapper));
}
Stream<T> filter_HeadedStream(Predicate<T> predicate) {
	return new_???(/*???*/);
}
boolean allMatch_HeadedStream(Predicate<T> predicate) {
	return fold(true, /*???*/&&predicate.test(t));
}
boolean anyMatch_HeadedStream(Predicate<T> predicate) {
	return fold(false, /*???*/);
}
