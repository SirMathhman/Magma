package org.example.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class OptionTest {
	@Test
	public void someBasic() {
		Option<String> o = Option.some("hello");
		assertTrue(o.isSome());
		assertFalse(o.isNone());
		assertEquals("hello", o.get());
		assertEquals("HELLO", o.map(String::toUpperCase).get());
	}

	@Test
	public void noneBasic() {
		Option<String> o = Option.none();
		assertFalse(o.isSome());
		assertTrue(o.isNone());
		assertEquals("fallback", o.orElse("fallback"));
		assertEquals("fallback2", o.orElseGet(() -> "fallback2"));
		// ifPresent should not throw
		o.ifPresent(s -> fail("should not be called"));
	}

	@Test
	public void flatMapAndEquality() {
		Option<Integer> a = Option.some(2);
		Option<Integer> b = a.flatMap(x -> Option.some(x + 3));
		assertTrue(b.isSome());
		assertEquals(5, b.get());
		assertEquals(Option.none(), Option.none());
	}
}
