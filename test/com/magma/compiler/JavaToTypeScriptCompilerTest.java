package com.magma.compiler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the Java to TypeScript compiler.
 * Following TDD principles, we write tests before implementing the actual functionality.
 */
public class JavaToTypeScriptCompilerTest {
    
    private JavaToTypeScriptCompiler compiler;
    
    @BeforeEach
    void setUp() {
        compiler = new JavaToTypeScriptCompiler();
    }
    
    @Test
    void shouldCompileEmptyClass() {
        // Given
        String javaCode = "package com.example;\n\n" +
                          "public class EmptyClass {\n" +
                          "}\n";
        
        // When
        String typeScriptCode = compiler.compile(javaCode);
        
        // Then
        assertThat(typeScriptCode).isNotNull();
        assertThat(typeScriptCode).contains("export class EmptyClass");
        assertThat(typeScriptCode).doesNotContain("public");  // TypeScript doesn't use public keyword
    }
}