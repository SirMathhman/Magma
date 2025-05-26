import { Merger } from "../../../../magma/app/compile/merge/Merger";
import { Strings } from "../../../../magma/api/text/Strings";
export class ValueMerger implements Merger {
	merge(cache: string, element: string): string {
		if (Strings.isEmpty(cache)/*unknown*/){
			return cache + element/*unknown*/;
		}
		return cache + ", " + element/*unknown*/;
	}
}
