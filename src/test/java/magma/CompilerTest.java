package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {

	@Test
	public void read() {
		var res = Runner.run("intrinsic fn readInt() : I32; readInt()", "5");
		switch (res) {
			case Result.Ok(var value) -> {
				assertEquals(new Tuple<>("", 5), value);
			}
			case Result.Err(var error) -> {
				fail(error);
			}
		}
	}
}
