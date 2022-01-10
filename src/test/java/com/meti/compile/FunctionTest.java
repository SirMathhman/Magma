package com.meti.compile;

import org.junit.jupiter.api.Test;

import static com.meti.compile.CompiledTest.assertSourceCompile;

public class FunctionTest {
    @Test
    void empty() {
        assertSourceCompile("def empty() : Void => {}", "void empty(){}");
    }

    @Test
    void one_parameter() {
        assertSourceCompile("def one_parameter(value : I16) : Void => {}", "void one_parameter(int value){}");
    }

    @Test
    void two_parameters(){
        assertSourceCompile("def Point(x : I16, y : I16) : Void => {}", "void Point(int x,int y){}");
    }

    @Test
    void return_type() {
        assertSourceCompile("def test() : U16 => {return 0;}", "unsigned int test(){return 0;}");
    }
}
