import { MethodHeader } from "../../../../magma/app/compile/define/MethodHeader";
export class ConstructorHeader implements MethodHeader {
	generateWithAfterName(afterName: string): string {
		return "constructor " + afterName/*unknown*/;
	}
	hasAnnotation(annotation: string): boolean {
		return false/*unknown*/;
	}
	removeModifier(modifier: string): MethodHeader {
		return this/*unknown*/;
	}
}
