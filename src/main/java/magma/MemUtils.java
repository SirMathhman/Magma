package magma;

public class MemUtils {
	@Actual
	public static <T> T[] malloc(int length) {
		return (T[]) new Object[length];
	}

}
