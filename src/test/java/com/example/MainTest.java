package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {
    private PrintStream originalOut;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void teardown() {
        System.setOut(originalOut);
    }

    @Test
    void printsTranspiledSource() throws IOException {
        Path temp = Files.createTempFile("Example", ".java");
        Files.writeString(temp, "package a;\npublic class A {}");

        Main.main(new String[] { temp.toString() });

        String expected = "export default class A {}" + System.lineSeparator();
        assertEquals(expected, out.toString());
    }
}
