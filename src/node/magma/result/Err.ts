import { Result } from './Result';

/**
 * An Err variant of Result representing a failed operation.
 *
 * @template T The type of the value (unused in this variant)
 * @template E The type of the error
 */
export class Err<T, E> implements Result<T, E> {
	/**
	 * Creates a new Err result with the given error.
	 *
	 * @param error The error of the result
	 */
	constructor(public readonly error: E) {}

	/**
	 * Checks if this result is an Err variant.
	 *
	 * @returns true for Err variant
	 */
	isErr(): boolean {
		return true;
	}

	/**
	 * Gets the value of this result.
	 * This will always throw an error for Err variant.
	 *
	 * @throws Error Cannot get value from Err result
	 */
	value(): T {
		throw new Error("Cannot get value from Err result");
	}
}
