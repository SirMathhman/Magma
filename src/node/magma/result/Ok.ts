import { Result } from './Result';

/**
 * An Ok variant of Result representing a successful operation.
 *
 * @template T The type of the value
 * @template E The type of the error (unused in this variant)
 */
export class Ok<T, E> implements Result<T, E> {
	/**
	 * Creates a new Ok result with the given value.
	 *
	 * @param value The value of the result
	 */
	constructor(public readonly value: T) {}

	/**
	 * Checks if this result is an Err variant.
	 *
	 * @returns false for Ok variant
	 */
	isErr(): boolean {
		return false;
	}

	/**
	 * Gets the error of this result.
	 * This will always throw an error for Ok variant.
	 *
	 * @throws Error Cannot get error from Ok result
	 */
	error(): E {
		throw new Error("Cannot get error from Ok result");
	}
}
