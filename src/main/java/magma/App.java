package magma;

/**
 * Simple application entrypoint for the Maven project scaffold.
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello from Magma!");
		System.out.println("Java version: " + System.getProperty("java.version"));
	}

	public static String greet(String name) {
		return "Hello, " + name + "!";
	}
}
