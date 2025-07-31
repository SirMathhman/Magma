package com.magma.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A self-hosted compiler that can compile itself from Java to TypeScript.
 * This class extends the JavaToTypeScriptCompilerV2 with the ability to read
 * Java source files and write TypeScript output files.
 */
public class SelfHostedCompiler extends JavaToTypeScriptCompilerV2 {
    
    /**
     * Compiles a Java source file to TypeScript.
     *
     * @param inputPath The path to the Java source file
     * @param outputPath The path where the TypeScript output should be written
     * @return true if compilation was successful, false otherwise
     */
    public boolean compileFile(String inputPath, String outputPath) {
        try {
            // Read the Java source file
            String javaCode = readFile(inputPath);
            
            // Compile the Java code to TypeScript
            String typeScriptCode = compile(javaCode);
            
            // Write the TypeScript code to the output file
            writeFile(outputPath, typeScriptCode);
            
            return true;
        } catch (IOException e) {
            System.err.println("Error compiling file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Compiles all Java source files in a directory to TypeScript.
     *
     * @param inputDir The directory containing Java source files
     * @param outputDir The directory where TypeScript output should be written
     * @return The number of files successfully compiled
     */
    public int compileDirectory(String inputDir, String outputDir) {
        try {
            // Create the output directory if it doesn't exist
            Path outputPath = Paths.get(outputDir);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            // Get all Java files in the input directory
            List<Path> javaFiles = findJavaFiles(inputDir);
            int successCount = 0;
            
            // Compile each Java file
            for (Path javaFile : javaFiles) {
                String fileName = javaFile.getFileName().toString();
                String tsFileName = fileName.replace(".java", ".ts");
                String outputFilePath = outputDir + "/" + tsFileName;
                
                if (compileFile(javaFile.toString(), outputFilePath)) {
                    successCount++;
                    System.out.println("Compiled: " + javaFile + " -> " + outputFilePath);
                }
            }
            
            return successCount;
        } catch (IOException e) {
            System.err.println("Error compiling directory: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Compiles itself (the compiler's own source code) to TypeScript.
     *
     * @param outputDir The directory where TypeScript output should be written
     * @return true if self-compilation was successful, false otherwise
     */
    public boolean compileSelf(String outputDir) {
        try {
            // Get the path to the compiler's source directory
            String sourceDir = getCompilerSourceDir();
            
            // Compile the compiler's source directory
            int compiledFiles = compileDirectory(sourceDir, outputDir);
            
            return compiledFiles > 0;
        } catch (Exception e) {
            System.err.println("Error during self-compilation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the directory containing the compiler's source code.
     *
     * @return The path to the compiler's source directory
     */
    private String getCompilerSourceDir() {
        // Get the path to the class file
        String classPath = SelfHostedCompiler.class.getProtectionDomain()
                                                  .getCodeSource()
                                                  .getLocation()
                                                  .getPath();
        
        // If we're running from a JAR, extract the path
        if (classPath.endsWith(".jar")) {
            classPath = classPath.substring(0, classPath.lastIndexOf("/"));
        }
        
        // Navigate to the source directory
        // This assumes a standard Maven/Gradle project structure
        return Paths.get(classPath)
                   .getParent()  // out
                   .getParent()  // target or build
                   .getParent()  // project root
                   .resolve("src/java/com/magma/compiler")
                   .toString();
    }
    
    /**
     * Reads a file and returns its contents as a string.
     *
     * @param filePath The path to the file
     * @return The contents of the file
     * @throws IOException If an I/O error occurs
     */
    private String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
    
    /**
     * Writes a string to a file.
     *
     * @param filePath The path to the file
     * @param content The content to write
     * @throws IOException If an I/O error occurs
     */
    private void writeFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }
    
    /**
     * Finds all Java files in a directory.
     *
     * @param directory The directory to search
     * @return A list of paths to Java files
     * @throws IOException If an I/O error occurs
     */
    private List<Path> findJavaFiles(String directory) throws IOException {
        List<Path> javaFiles = new ArrayList<>();
        
        Files.walk(Paths.get(directory))
             .filter(path -> path.toString().endsWith(".java"))
             .forEach(javaFiles::add);
        
        return javaFiles;
    }
}