import { Locator } from "../../../../magma/app/compile/locate/Locator";
export class FirstLocator implements Locator {
	apply(input1: string, infix1: string): number {
		return input1.indexOf(infix1)/*unknown*/;
	}
}
