// File generated from '.\src\main\java\magma\Streams.java'. This is not source code!
#include "Main.h"
Stream<T> new_Stream<T>(void* _ref, Supplier<Option<T>> head){
	Streams this;
	/*<R> Stream<R> map(Function<T, R> mapper) {
			return new Stream<R>(() -> this.head.get().map(mapper));
		}*/
	/*public Stream<T> filter(Predicate<T> predicate) {
			return new Stream<T>(() -> {
				while (true) {
					Option<T> nextValue = this.head.get();
					if (nextValue instanceof Some<T>(T value)) {
						if (predicate.test(value)) {
							return new Some<T>(value);
						}
						// Continue to next element
					} else {
						return new None<T>();
					}
				}
			});
		}*/
	/*public <C> C collect(Collector<T, C> collector) {
			return this.foldWithInitial(collector.createInitial(), collector::fold);
		}*/
	/*public <C> C foldWithInitial(C initial, BiFunction<C, T, C> folder) {
			C accumulator = initial;
			Option<T> current = this.head.get();
			while (current instanceof Some<T>(T value)) {
				accumulator = folder.apply(accumulator, value);
				current = this.head.get();
			}
			return accumulator;
		}*/
	/*public <R> Stream<R> flatMap(Function<T, Stream<R>> mapper) {
			return new Stream<R>(new FlatMapHead<T, R>(this.head, mapper));
		}*/
	/*public Option<T> fold(BiFunction<T, T, T> folder) {
			return this.<Option<T>>foldWithInitial(new None<T>(), (current, element) -> {
				if (current instanceof None<T>) {
					return new Some<T>(element);
				}
				return current.map(inner -> folder.apply(inner, element));
			});
		}*/
	/*public Option<T> next() {
			return this.head.get();
		}*/
	/*public <R> Stream<Tuple<T, R>> zip(Stream<R> stream) {
			return new Stream<Tuple<T, R>>(() -> this.head.get().and(stream::next));
		}*/
	return this;
}
Stream<T> fromRef_Streams(void* _ref, T* elements){
	return new_Stream<T>(new_ArrayHead<T>(elements, elements.length));
}
Stream<Integer> fromLength_Streams(void* _ref, int length){
	return new_Stream<Integer>(RangeHead.createRangeStream(length));
}
Stream<T> fromOption_Streams(void* _ref, Option<T> option){
	return new_Stream<T>(/*switch (option) {
			case None<T> _ -> new EmptyHead<T>();
			case Some<T> v -> new SingletonHead<T>(v.value());
		}*/);
}
int main(){
	main_Main();
	return 0;
}