// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
/* 

public class Main {
    public static void main(String[] args) {
        try {
            Path path = Paths.get(".", "src", "java", "magma", "Main.java"); *//* 
            String input = Files.readString(path); *//* 

            Path target = path.resolveSibling("main.c"); *//* 
            Files.writeString(target, compile(input)); *//* 
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace(); *//* 
        }
    }

    private static String compile(String input) {
        ArrayList<String> segments = new ArrayList<>(); *//* 
        StringBuilder buffer = new StringBuilder(); *//* 
        for (int i = 0; *//*  i < input.length(); *//*  i++) {
            char c = input.charAt(i); *//* 
            buffer.append(c); *//* 
            if (c == '; *//* ') {
                segments.add(buffer.toString()); *//* 
                buffer = new StringBuilder(); *//* 
            }
        }
        segments.add(buffer.toString()); *//* 

        StringBuilder output = new StringBuilder(); *//* 
        for (String segment : segments) {
            output.append(compileRootSegment(segment)); *//* 
        }

        return output.toString(); *//* 
    }

    private static String compileRootSegment(String input) {
        if (input.startsWith("package ")) {
            return ""; *//* 
        }
        if (input.strip().startsWith("import ")) {
            return "// #include <temp.h>\n"; *//* 
        }
        return generatePlaceholder(input); *//* 
    }

    private static String generatePlaceholder(String input) {
        return "/* " + input + " */"; *//* 
    }
}
 */