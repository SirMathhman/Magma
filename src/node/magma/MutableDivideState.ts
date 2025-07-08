export class MutableDivideState implements DivideState {
	private readonly segments : /*Collection<String>*/ = /*new ArrayList<>()*/;
	private buffer : /*StringBuilder*/ = /*new StringBuilder()*/;
	private depth : /*int*/ = /*0*/;
	/*@Override
    public Stream<String> stream() {
        return this.segments.stream();
    }*/
	/*@Override
    public DivideState append(final char c) {
        this.buffer.append(c);
        return this;
    }*/
	/*@Override
    public DivideState advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }*/
	/*@Override
    public boolean isLevel() {
        return 0 == this.depth;
    }*/
	/*@Override
    public DivideState enter() {
        this.depth++;
        return this;
    }*/
	/*@Override
    public DivideState exit() {
        this.depth--;
        return this;
    }*/
	/*@Override
    public boolean isShallow() {
        return 1 == this.depth;
    }*/
	/**/
}/**/