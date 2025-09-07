/* Generated header for com.example.magma.compiler.Compiler */
#ifndef COM_EXAMPLE_MAGMA_COMPILER_COMPILER_H
#define COM_EXAMPLE_MAGMA_COMPILER_COMPILER_H

/* Original Java source:
    package com.example.magma.compiler;
    
    import com.example.magma.model.Location;
    import com.example.magma.util.Tuple;
    
    import java.util.HashMap;
    import java.util.Map;
    
    /**
     * Compiler that converts Java source strings into C and H file contents.
     *
     * This implementation is a stub: it produces a simple header and C file
     * using the Location and the Java source as comments. Behavior can be
     * extended to perform real translation.
     */
    public class Compiler {
    
    	/**
    	 * Compile a map of Location -> javaSource to a map of Location -> (cContent,
    	 * hContent).
    	 * The Tuple contains (cContent, hContent) in that order.
    	 */
    	public Map<Location, Tuple<String, String>> compile(Map<Location, String> sources) {
    		Map<Location, Tuple<String, String>> out = new HashMap<>();
    
    		for (Map.Entry<Location, String> e : sources.entrySet()) {
    			Location loc = e.getKey();
    			String src = e.getValue();
    
    			String baseName = loc.className();
    
    			String header = "/* Generated header for " + loc.toString() + " */\n"
    					+ "#ifndef " + makeGuard(loc) + "\n"
    					+ "#define " + makeGuard(loc) + "\n\n"
    					+ "/* Original Java source:\n" + indent(src) + "\n*/\n\n"
    					+ "#endif /* " + makeGuard(loc) + " */\n";
    
    			String c = "/* Generated C for " + loc.toString() + " */\n"
    					+ "#include \"" + baseName + ".h\"\n\n"
    					+ "/* Original Java source:\n" + indent(src) + "\n*/\n";
    
    			out.put(loc, Tuple.of(c, header));
    		}
    
    		return out;
    	}
    
    	private static String makeGuard(Location loc) {
    		String s = (loc.packageAsString().isEmpty() ? loc.className() : loc.packageAsString() + "." + loc.className());
    		s = s.replaceAll("[^A-Za-z0-9]", "_").toUpperCase();
    		if (!s.endsWith("_H"))
    			s = s + "_H";
    		return s;
    	}
    
    	private static String indent(String s) {
    		String[] lines = s.split("\\R", -1);
    		StringBuilder sb = new StringBuilder();
    		for (String line : lines) {
    			sb.append("    ").append(line).append("\n");
    		}
    		return sb.toString();
    	}
    }
    

*/

#endif /* COM_EXAMPLE_MAGMA_COMPILER_COMPILER_H */
