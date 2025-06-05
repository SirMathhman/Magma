// Auto-generated from magma/MethodBodyParser.java
export class MethodBodyParser {
	static parseSegments(body: string): List<string> {
		let segments: List<string> = new ArrayList<>();
		let indent: number = 0;
		for (String token : tokens(body)) {
			let stripped: string = token.trim();
			if (stripped.isEmpty()) {
			}
			if (stripped.startsWith("}
				indent = Math.max(0, indent - 1);
				stripped = stripped.substring(1).trim();
				if (stripped.isEmpty()) {
				}
	}
}
