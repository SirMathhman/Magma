import { Locator } from "../../../../magma/app/compile/locate/Locator";
export class LastLocator implements Locator {
	apply(input1: string, infix1: string): number {
		return input1.lastIndexOf(infix1)/*unknown*/;
	}
}
