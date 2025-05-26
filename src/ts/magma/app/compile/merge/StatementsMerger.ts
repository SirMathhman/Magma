import { Merger } from "../../../../magma/app/compile/merge/Merger";
export class StatementsMerger implements Merger {
	merge(cache: string, element: string): string {
		return cache + element/*unknown*/;
	}
}
