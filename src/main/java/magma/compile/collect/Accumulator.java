package magma.compile.collect;

import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record Accumulator<T>(Option<T> option, List<CompileError> errors) {
	public Accumulator() {
		this(new None<>(), new ArrayList<>());
	}

	public static <T, R> Result<R, List<CompileError>> merge(List<T> elements,
																													 Function<T, Result<R, CompileError>> mapper) {
		return elements.stream().reduce(new Accumulator<R>(), (accumulator, rule) -> switch (mapper.apply(rule)) {
			case Err<R, CompileError> v -> accumulator.addError(v.error());
			case Ok<R, CompileError> v -> accumulator.setValue(v.value());
		}, (_, next) -> next).toResult();
	}

	public Accumulator<T> addError(CompileError error) {
		errors.add(error);
		return this;
	}

	public Accumulator<T> setValue(T value) {
		return new Accumulator<>(new Some<>(value), errors);
	}

	public Result<T, List<CompileError>> toResult() {
		return switch (option) {
			case None<T> _ -> new Err<>(errors);
			case Some<T> v -> new Ok<>(v.value());
		};
	}
}
