package magma;

import magma.Collections.ArrayList;
import magma.Options.Option;
import magma.Results.Result;
import magma.Streams.Stream;

public class IO {
	public interface IOError {
		String display();
	}

	public interface Path {
		boolean exists();

		Result<String, IOError> readString();

		Option<IOError> createDirectories();

		Option<IOError> writeString(String output);

		Path getParent();

		Result<ArrayList<Path>, IOError> walk();

		String asString();

		Path relativize(Path path);

		Path resolveByPath(Path path);

		Stream<String> stream();

		Path getFileName();

		Path resolveByString(String name);
	}
}
