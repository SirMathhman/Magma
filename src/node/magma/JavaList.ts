/*package magma;*/
/*import java.util.ArrayList;*/
/*import java.util.List;*/
/*import java.util.stream.Stream;*/
/*public record JavaList<T>(List<T> list) implements ListLike<T> */ {
	/*public JavaList*/(/**/) {/*
        this(new ArrayList<>());
    */}
	/*@Override public*/ stream : /*Stream<T>*/(/**/) {/*
        return this.list.stream();
    */}
	/*@Override public*/ add : /*ListLike<T>*/(/*final T element*/) {/*
        this.list.add(element);
        return this;
    */}
	/**/}
/**/
