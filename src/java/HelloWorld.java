import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        
        try {
            // Get the project root directory (one level up from src)
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path projectRoot = currentPath;
            if (currentPath.toString().endsWith("\\src\\java")) {
                projectRoot = currentPath.getParent().getParent();
            }
            
            // Create src/windows directory at the same level as src/java
            File directory = new File(projectRoot.toString(), "src\\windows");
            directory.mkdirs();
            
            // Ensure we don't have nested src/java/src/windows
            File nestedDir = new File(currentPath.toString(), "src\\windows");
            if (nestedDir.exists()) {
                // Delete the nested directory and its contents
                File[] files = nestedDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                nestedDir.delete();
            }
            
            // Create empty C file
            Path filePath = Paths.get(directory.toString(), "Main.c");
            Files.write(filePath, new byte[0]);
            System.out.println("Empty C file created at " + filePath);
        } catch (IOException e) {
            System.err.println("Error creating C file: " + e.getMessage());
        }
    }
}