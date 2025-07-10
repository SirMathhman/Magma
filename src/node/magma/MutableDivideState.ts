/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.Optional;*/
/*import java.util.stream.Stream;*/
/**/class MutableDivideState implements DivideState {
	private readonly segments : /*Collection<String>*/ = /*new ArrayList<>*/();
	/*private final CharSequence input*/;
	private index : int = /* 0*/;
	private buffer : StringBuilder = /*new StringBuilder*/();
	private depth : int = /* 0*/;
	/*MutableDivideState(final CharSequence input) {
        this.input = input;
    }*/
	public advance() : DivideState {
		/*this.segments.add(this.buffer.toString())*/;
		/*this.buffer */ = /*new StringBuilder*/();
		/*return this*/;
	}
	public append(c : char) : DivideState {
		/*this.buffer.append(c)*/;
		/*return this*/;
	}
	public stream() : /*Stream<String>*/ {
		/*return this.segments.stream()*/;
	}
	public isLevel() : boolean {
		/*return 0 */ = /*= this*/.depth;
	}
	public enter() : DivideState {
		/*this.depth++*/;
		/*return this*/;
	}
	public exit() : DivideState {
		/*this.depth--*/;
		/*return this*/;
	}
	public pop() : /*Character>>*/ {
		let > : /*(this.index*/ = this.input.length(/*)) return Optional.empty(*/);
		const c = this.input.charAt(/*this.index*/);
		/*this.index++*/;
		/*return Optional.of(new Tuple<>(this, c))*/;
	}
	public popAndAppendToTuple() : /*Character>>*/ {
		/*return this.pop().map(tuple -> {
            final var appended = tuple.left().append(tuple.right());
            return new Tuple<>(appended, tuple.right());
        }*/
		/*)*/;
	}
	public popAndAppendToOption() : /*Optional<DivideState>*/ {
		/*return this.popAndAppendToTuple().map(Tuple::left)*/;
	}
	public isShallow() : boolean {
		/*return 1 */ = /*= this*/.depth;
	}
	/**/}/**/
