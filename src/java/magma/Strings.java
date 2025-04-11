package magma;

public class Strings {
    public static String unwrap(Main.String_ string) {
        return new String(string.array());
    }

    public static Main.String_ from(char[] array) {
        return new Main.String_(array);
    }
}
