package org.example;

import org.junit.jupiter.api.Test;

/**
 * Recursion should be banned. A self-recursive function call must be invalid.
 */
public class RecursionBanTest {

	@Test
	void selfRecursiveFunctionIsInvalid() {
		// Clearly recursive: calling itself directly with no base case
		// Expectation: interpreter should reject recursion (InterpretingException)
		TestUtils.assertInvalid("fn self() : I32 => self(); self()");
	}
}
