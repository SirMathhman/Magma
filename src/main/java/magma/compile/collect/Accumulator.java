package magma.compile.collect;

import magma.compile.error.CompileError;
import magma.list.ArrayList;
import magma.list.List;
import magma.list.Stream;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.function.Function;

public record Accumulator<T>(Option<T> option, List<CompileError> errors) {
	public Accumulator() {
		this(new None<T>(), new ArrayList<CompileError>());
	}

	public static <T, R> Result<R, List<CompileError>> merge(List<T> elements,
																													 Function<T, Result<R, CompileError>> mapper) {
		final Accumulator<R> identity = new Accumulator<R>();
		final Stream<T> stream = elements.stream();
		final Accumulator<R> reduce =
				stream.reduce(identity, (accumulator, rule) -> fold(mapper, accumulator, rule));
		return reduce.toResult();
	}

	private static <T, R> Accumulator<R> fold(Function<T, Result<R, CompileError>> mapper,
																						Accumulator<R> accumulator,
																						T rule) {
		return switch (mapper.apply(rule)) {
			case Err<R, CompileError> v -> accumulator.addError(v.error());
			case Ok<R, CompileError> v -> accumulator.setValue(v.value());
		};
	}

	public Accumulator<T> addError(CompileError error) {
		errors.addLast(error);
		return this;
	}

	public Accumulator<T> setValue(T value) {
		return new Accumulator<T>(new Some<T>(value), errors);
	}

	public Result<T, List<CompileError>> toResult() {
		return switch (option) {
			case None<T> _ -> new Err<T, List<CompileError>>(errors);
			case Some<T> v -> new Ok<T, List<CompileError>>(v.value());
		};
	}
}
