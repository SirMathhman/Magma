/**
 * A simple Hello World program for the Magma project.
 */
public class Main {
	/**
	 * The main entry point for the application.
	 *
	 * @param args Command line arguments (not used)
	 */
	public static void main(String[] args) {
		System.out.println("Hello, World!");
		System.out.println("Sum of 5 and 3 is: " + add(5, 3));
	}
	
	/**
	 * A simple method that adds two integers.
	 *
	 * @param a first integer
	 * @param b second integer
	 * @return the sum of a and b
	 */
	public static int add(int a, int b) {
		return a + b;
	}
}