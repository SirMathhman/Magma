/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.Optional;*/
/*import java.util.stream.Stream;*/
/**/class MutableDivideState implements DivideState {
	private readonly segments : /*Collection<String>*/ = /*new ArrayList<>*/();
	/*private final CharSequence input*/;
	private index : /*int*/ = /* 0*/;
	private buffer : /*StringBuilder*/ = /*new StringBuilder*/();
	private depth : /*int*/ = /* 0*/;
	/*MutableDivideState(final CharSequence input) {
        this.input = input;
    }*/
	advance : /*DivideState*/(){
		/*this.segments.add(this.buffer.toString());*/
		/*this.buffer = new StringBuilder();*/
		/*return this;*/
	}
	append : /*DivideState*/(/*final char c*/){
		/*this.buffer.append(c);*/
		/*return this;*/
	}
	stream : /*Stream<String>*/(){
		/*return this.segments.stream();*/
	}
	isLevel : /*boolean*/(){
		/*return 0 == this.depth;*/
	}
	enter : /*DivideState*/(){
		/*this.depth++;*/
		/*return this;*/
	}
	exit : /*DivideState*/(){
		/*this.depth--;*/
		/*return this;*/
	}
	pop : /*Character>>*/(){
		/*if (this.index >= this.input.length()) return Optional.empty();*/
		/*final var c = this.input.charAt(this.index);*/
		/*this.index++;*/
		/*return Optional.of(new Tuple<>(this, c));*/
	}
	popAndAppendToTuple : /*Character>>*/(){
		/*return this.pop().map(tuple -> {
            final var appended = tuple.left().append(tuple.right());
            return new Tuple<>(appended, tuple.right());
        }*/
		/*);*/
	}
	popAndAppendToOption : /*Optional<DivideState>*/(){
		/*return this.popAndAppendToTuple().map(Tuple::left);*/
	}
	isShallow : /*boolean*/(){
		/*return 1 == this.depth;*/
	}
	/**/}/**/
