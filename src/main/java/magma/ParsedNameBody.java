package magma;

class ParsedNameBody {
	public final String name;
	public final String body;
	public final String remainder;

	ParsedNameBody(String name, String body, String remainder) {
		this.name = name;
		this.body = body;
		this.remainder = remainder;
	}
}
