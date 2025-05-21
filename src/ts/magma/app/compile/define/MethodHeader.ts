export interface MethodHeader {
	generateWithAfterName(afterName: string): string;
	hasAnnotation(annotation: string): boolean;
	removeModifier(modifier: string): MethodHeader;
}
