export interface Node {
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
