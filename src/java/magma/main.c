/* 

import java.io.IOException; *//* 
import java.nio.file.Files; *//* 
import java.nio.file.Path; *//* 
import java.nio.file.Paths; *//* 
import java.util.ArrayList; *//* 

public class Main {
    public static void main(String[] args) {
        try {
            Path source = Paths.get(".", "src", "java", "magma", "Main.java"); *//* 
            String input = Files.readString(source); *//* 

            Path target = source.resolveSibling("main.c"); *//* 
            Files.writeString(target, compile(input) + "int main(){\n\treturn 0; *//* \n}"); *//* 

            Process process = new ProcessBuilder("cmd.exe", "/c", "build.bat")
                    .directory(Paths.get(".").toFile())
                    .inheritIO()
                    .start(); *//* 

            process.waitFor(); *//* 
        } catch (IOException | InterruptedException e) {
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

        return generatePlaceholder(input); *//* 
    }

    private static String generatePlaceholder(String input) {
        String replaced = input
                .replace("<cmt-start>", "<cmt-start>")
                .replace("<cmt-end>", "<cmt-end>"); *//* 

        return "<cmt-start>" + replaced + "<cmt-end>"; *//* 
    }
}
 */int main(){
	return 0;
}