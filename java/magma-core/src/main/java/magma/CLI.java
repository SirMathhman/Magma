package magma;

/**
 * A tiny CLI entrypoint for the stubbed Magma project.
 * Prints help and exits normally.
 * See docs/architecture.md for design.
 */
public final class CLI {
	public static void main(String[] args) {
		if (args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
			System.out.println("magma-cli (stub)\nusage: magma [options] <file>");
			System.exit(0);
		}

		// For now, accept any argument and return success.
		System.out.println("magma: received " + args.length + " argument(s)");
		System.exit(0);
	}
}
