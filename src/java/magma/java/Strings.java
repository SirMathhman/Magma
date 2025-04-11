package magma.java;

import magma.Main;

public class Strings {
    public static String toNativeString(Main.String_ string) {
        return new String(string.array());
    }

    public static Main.String_ fromNativeString(String array) {
        return new Main.String_(array.toCharArray());
    }
}
