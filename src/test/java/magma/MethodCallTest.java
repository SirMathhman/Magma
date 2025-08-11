package magma;

import org.junit.jupiter.api.Test;

class MethodCallTest extends CompilerTestBase {

    @Test
    void methodCallTransformation() {
        String input = "class fn Calculator() => {\n" +
                       "    fn add(first : I32, second : I32) => {\n" +
                       "        return first + second;\n" +
                       "    }\n" +
                       "}\n" +
                       "\n" +
                       "fn main() => {\n" +
                       "    let myCalculator = Calculator();\n" +
                       "    let result = myCalculator.add(1, 2);\n" +
                       "    return 0;\n" +
                       "}";

        String expected = "struct Calculator {\n" +
                         "};\n" +
                         "\n" +
                         "int32_t add_Calculator(struct Calculator* this, int32_t first, int32_t second) {\n" +
                         "    return first + second;\n" +
                         "}\n" +
                         "\n" +
                         "struct Calculator Calculator() {\n" +
                         "    struct Calculator this;\n" +
                         "    return this;\n" +
                         "}\n" +
                         "\n" +
                         "int32_t main() {\n" +
                         "    struct Calculator myCalculator = Calculator();\n" +
                         "    int32_t result = add_Calculator(&myCalculator, 1, 2);\n" +
                         "    return 0;\n" +
                         "}";

        assertPrettyValid(input, expected);
    }
}