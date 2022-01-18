package com.meti.app;

import com.meti.api.io.Directory;
import com.meti.api.io.File;
import com.meti.api.io.NIOPath;
import com.meti.api.io.Path;
import com.meti.app.module.SingleModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.meti.app.Application.Out;
import static org.junit.jupiter.api.Assertions.*;

public class SingleApplicationTest {
    private static final Path Source = NIOPath.Root.resolveChild("index.mg");
    private static final Path Target = Out.resolveChild("index.c");

    @Test
    void creates_target() throws IOException {
        Source.createAsFile().writeAsString("def main() : I16 => {return 0;}");
        runImpl();
        assertTrue(Target.exists());
    }

    private void runImpl() {
        try {
            new Application(new SingleModule(Source)).run();
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    @Test
    void does_not_create_target() {
        runImpl();
        assertFalse(Target.exists());
    }

    @Test
    void empty() throws IOException {
        Source.createAsFile();
        runImpl();
        assertTrue(Target.existingAsFile().isEmpty());
    }

    @AfterEach
    void tearDown() throws IOException {
        Source.existingAsFile().ifPresent(File::delete);
        Out.existingAsDirectory().ifPresent(Directory::deleteAsDirectory);
    }

    @Test
    void test_main() throws IOException {
        Source.createAsFile().writeAsString("def main() : I16 => {return 0;}");
        runImpl();
        assertEquals("#include \"index.h\"\nint main(){return 0;}", Target.existingAsFile()
                .map(File::readAsString)
                .orElse(""));
    }
}