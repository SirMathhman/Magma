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

 /**
  * A method that accepts a string and returns a string without doing anything useful.
  *
  * @param input the input string
  * @return the input string (possibly with minor modifications)
  */
 public static String processString(String input) {
 	return input;
 }
}