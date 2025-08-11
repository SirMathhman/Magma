package magma;

import org.junit.jupiter.api.Test;

class CStrTest extends CompilerTestBase {
	@Test
	void basicCStr() {
		assertValid("let x : *CStr = \"Hello World!\";", "char* x = \"Hello World!\";");
	}

	@Test
	void cstrEmpty() {
		assertValid("let empty : *CStr = \"\";", "char* empty = \"\";");
	}

	@Test
	void cstrWithSpaces() {
		assertValid("let message : *CStr = \"Hello there, world!\";", "char* message = \"Hello there, world!\";");
	}

	@Test
	void cstrMutable() {
		assertValid("let mut buffer : *CStr = \"test\";", "char* buffer = \"test\";");
	}

	@Test
	void cstrWithSpecialChars() {
		assertValid("let message : *CStr = \"Hello, World! 123\";", "char* message = \"Hello, World! 123\";");
	}
}