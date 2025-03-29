package magma.result;class package magma.result;{package magma.result;

import magma.option.None;class import magma.option.None;{

import magma.option.None;
import magma.option.Option;class import magma.option.Option;{
import magma.option.Option;
import magma.option.Some;class import magma.option.Some;{
import magma.option.Some;
import magma.option.Tuple;class import magma.option.Tuple;{
import magma.option.Tuple;

import java.util.function.Function;class import java.util.function.Function;{

import java.util.function.Function;
import java.util.function.Supplier;class import java.util.function.Supplier;{
import java.util.function.Supplier;

public record Err<T, X>(X error) implements Result<T, X> {
    @Override
    public Option<T> findValue() {
        return new None<>();
    }

    @Override
    public Option<X> findError() {
        return new Some<>(error);
    }

    @Override
    public <R> Result<R, X> mapValue(Function<T, R> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<T, R> mapErr(Function<X, R> mapper) {
        return new Err<>(mapper.apply(error));
    }

    @Override
    public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
        return whenErr.apply(error);
    }

    @Override
    public <R> Result<Tuple<T, R>, X> and(Supplier<Result<R, X>> other) {
        return new Err<>(error);
    }
}
class public record Err<T, X>(X error) implements Result<T, X> {
    @Override
    public Option<T> findValue() {
        return new None<>();
    }

    @Override
    public Option<X> findError() {
        return new Some<>(error);
    }

    @Override
    public <R> Result<R, X> mapValue(Function<T, R> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<T, R> mapErr(Function<X, R> mapper) {
        return new Err<>(mapper.apply(error));
    }

    @Override
    public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
        return whenErr.apply(error);
    }

    @Override
    public <R> Result<Tuple<T, R>, X> and(Supplier<Result<R, X>> other) {
        return new Err<>(error);
    }
}{

public record Err<T, X>(X error) implements Result<T, X> {
    @Override
    public Option<T> findValue() {
        return new None<>();
    }

    @Override
    public Option<X> findError() {
        return new Some<>(error);
    }

    @Override
    public <R> Result<R, X> mapValue(Function<T, R> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<T, R> mapErr(Function<X, R> mapper) {
        return new Err<>(mapper.apply(error));
    }

    @Override
    public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
        return whenErr.apply(error);
    }

    @Override
    public <R> Result<Tuple<T, R>, X> and(Supplier<Result<R, X>> other) {
        return new Err<>(error);
    }
}
