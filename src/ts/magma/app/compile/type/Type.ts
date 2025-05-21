export interface Type {
	isFunctional(): boolean;
	isVar(): boolean;
	generateBeforeName(): string;
	generateSimple(): string;
}
