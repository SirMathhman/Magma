import { Iterable } from "../../../../magma/api/collect/list/Iterable";
export interface Merger {
	static generateAll(elements: Iterable<string>, merger: Merger): string {
		return elements.iter().foldWithInitial("", merger.merge)/*unknown*/;
	}
	merge(s: string, s2: string): string;
}
