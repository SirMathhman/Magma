package magma;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

	@Test
	void andCombinesTwoOkValues() {
		Result.Ok<Integer, String> left = new Result.Ok<>(1);
		Supplier<Result<Integer, String>> rightSupplier = () -> new Result.Ok<>(2);
		var res = left.and(rightSupplier);
		assertTrue(res instanceof Result.Ok);
		var ok = (Result.Ok<Result.Tuple<Integer, Integer>, String>) res;
		assertEquals(1, ok.value().first());
		assertEquals(2, ok.value().second());
	}

	@Test
	void andPropagatesLeftErr() {
		Result.Err<Integer, String> left = new Result.Err<>("left-err");
		Supplier<Result<Integer, String>> rightSupplier = () -> new Result.Ok<>(2);
		var res = left.and(rightSupplier);
		assertTrue(res instanceof Result.Err);
		var err = (Result.Err<Result.Tuple<Integer, Integer>, String>) res;
		assertEquals("left-err", err.error());
	}

	@Test
	void andPropagatesRightErr() {
		Result.Ok<Integer, String> left = new Result.Ok<>(1);
		Supplier<Result<Integer, String>> rightSupplier = () -> new Result.Err<>("right-err");
		var res = left.and(rightSupplier);
		assertTrue(res instanceof Result.Err);
		var err = (Result.Err<Result.Tuple<Integer, Integer>, String>) res;
		assertEquals("right-err", err.error());
	}

	@Test
	void andDoesNotCallSupplierWhenLeftErr() {
		Result.Err<Integer, String> left = new Result.Err<>("left-err");
		final boolean[] called = {false};
		Supplier<Result<Integer, String>> rightSupplier = () -> { called[0] = true; return new Result.Ok<>(2); };
		var res = left.and(rightSupplier);
		assertTrue(res instanceof Result.Err);
		assertFalse(called[0]);
	}

	@Test
	void andCallsSupplierWhenLeftOk() {
		Result.Ok<Integer, String> left = new Result.Ok<>(1);
		final boolean[] called = {false};
		Supplier<Result<Integer, String>> rightSupplier = () -> { called[0] = true; return new Result.Ok<>(2); };
		var res = left.and(rightSupplier);
		assertTrue(res instanceof Result.Ok);
		assertTrue(called[0]);
	}
}
