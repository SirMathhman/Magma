package magma;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

class BuildScriptTest {
    @Test
    void printsSuccessMessage() throws Exception {
        Process process = new ProcessBuilder("./build.sh")
                .directory(new File("."))
                .redirectErrorStream(true)
                .start();
        StringBuilder output = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        }
        int exit = process.waitFor();
        assertTrue(exit == 0);
        assertTrue(output.toString().contains("Build completed successfully"));
    }
}
