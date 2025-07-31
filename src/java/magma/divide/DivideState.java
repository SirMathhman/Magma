package magma.divide;

import magma.Tuple;

import java.util.Optional;
import java.util.stream.Stream;

public interface DivideState {
	Optional<Tuple<DivideState, Character>> pop();

	boolean isLevel();

	DivideState exit();

	DivideState enter();

	DivideState advance();

	DivideState append(char c);

	Stream<String> stream();
}
