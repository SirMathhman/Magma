import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the Main compiler class.
 * Tests the basic functionality of compiling Java to C.
 */
public class MainTest {

	/**
	 * Test that the compiler can generate C code for a Hello World program.
	 * This is the simplest test case for our Java to C compiler.
	 */
	@Test
	public void testCompileHelloWorld() {
		// Arrange
		String javaCode = """
				public class HelloWorld {
				    public static void main(String[] args) {
				        System.out.println("Hello, World!");
				    }
				}""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		assertNotNull(cCode, "Compiled C code should not be null");
		assertTrue(cCode.contains("#include <stdio.h>"), "C code should include stdio.h");
		assertTrue(cCode.contains("int main("), "C code should have a main function");
		assertTrue(cCode.contains("printf(\"Hello, World!"), "C code should print Hello World");
		assertTrue(cCode.contains("return 0;"), "C code should return 0");
	}
}