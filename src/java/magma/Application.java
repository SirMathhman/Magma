package magma;

public class Application {
 public static String run(String value) throws ApplicationException {
 	if (value != null && value.matches("\\d+")) {
 		return value;
 	} else {
 		throw new ApplicationException();
 	}
 }
}
