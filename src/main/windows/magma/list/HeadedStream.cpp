// Generated transpiled C++ from 'src\main\java\magma\list\HeadedStream.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct HeadedStream {Head</*???*/> head;};
struct FlatMapHead {Stream</*???*/> (*)(/*???*/) mapper;Option<Head</*???*/>> currentInnerHead;};
/*???*/ FlatMapHead_FlatMapHead(Stream</*???*/> (*mapper)(/*???*/)) {
	this.mapper=mapper;
	currentInnerHead=new_???();
}
Option</*???*/> next_FlatMapHead() {
	while (true)
	{
	if (/*???*/)
	{
	Option</*???*/> innerNext=innerHead.next();
	if (/*???*/)/*???*/ innerNext;
	currentInnerHead=new_???();}
	Option</*???*/> outerNext=head.next();
	if (/*???*/)
	{
	Stream</*???*/> innerStream=mapper.apply(value);
	if (/*???*/)currentInnerHead=new_???(head1);
	else
	return new_???();}
	else
	return new_???();}
}
Stream</*???*/> map_HeadedStream(/*???*/ (*mapper)(/*???*/)) {
	return new_???(/*???*/.next().map(mapper));
}
/*???*/ fold_HeadedStream(/*???*/ initial, BiFunction</*???*/, /*???*/, /*???*/> folder) {
	/*???*/ current=initial;
	while (true)
	{
	/*???*/ finalCurrent=current;
	Option</*???*/> map=head.next().map(/*???*/.apply(finalCurrent, inner));
	if (/*???*/)current=value;
	else
	/*???*/ current;}
}
/*???*/ collect_HeadedStream(Collector</*???*/, /*???*/> collector) {
	return fold(collector.initial(), /*???*/);
}
/*???*/ forEach_HeadedStream(Consumer</*???*/> consumer) {
	while (true)
	{
	Option</*???*/> next=head.next();
	if (/*???*/)consumer.accept(temp);
	else
	break}
}
Stream</*???*/> flatMap_HeadedStream(Stream</*???*/> (*mapper)(/*???*/)) {
	return new_???(new_???(mapper));
}
Stream</*???*/> filter_HeadedStream(Predicate</*???*/> predicate) {
	return new_???(/*???*/);
}
/*???*/ allMatch_HeadedStream(Predicate</*???*/> predicate) {
	return fold(true, /*???*/&&predicate.test(t));
}
/*???*/ anyMatch_HeadedStream(Predicate</*???*/> predicate) {
	return fold(false, /*???*/);
}
