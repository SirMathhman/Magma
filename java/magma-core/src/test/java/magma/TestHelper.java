package magma;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

public class TestHelper {
	/**
	 * Run the CLI in a separate JVM using the same classpath and return the exit
	 * code.
	 */
	public static int runCliWithArg(String arg) throws IOException, InterruptedException {
		String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");

		List<String> cmd = new ArrayList<>();
		cmd.add(javaBin);
		cmd.add("-cp");
		cmd.add(classpath);
		cmd.add("magma.CLI");
		cmd.add(arg);

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);

		Process p = pb.start();
		return p.waitFor();
	}
}
