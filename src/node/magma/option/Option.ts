// Auto-generated from magma/option/Option.java
export interface Option<T> {
	isPresent(): boolean;
	get(): T;
	ifPresent(action: (arg0: T) => void): void;
}
