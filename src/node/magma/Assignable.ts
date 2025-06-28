
interface Assignable extends MethodHeader permits Definition, Placeholder {
	generate() : string;
}

