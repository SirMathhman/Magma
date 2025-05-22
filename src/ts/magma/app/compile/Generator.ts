export interface Generator<T> {
	generate(value: T): string;
}
