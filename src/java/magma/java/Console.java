package magma.java;

import magma.Main;

public class Console {
    public static void printlnErr(Main.String_ content) {
        System.err.println(Strings.toNativeString(content));
    }
}
