package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class InterpreterTupleRobustnessTest {
	@Test
	void inlineTupleIndexing() {
		assertValid("[3,4][0]", "3");
		assertValid("[3,4][1]", "4");
	}

	@Test
	void letBoundTupleIndexing() {
		assertValid("let t = [10,20]; t[0]", "10");
		assertValid("let t = [10,20]; t[1]", "20");
	}

	@Test
	void tupleWithSpacesIndexing() {
		assertValid("[  7 , 8 ][ 1 ]", "8");
	}
}
