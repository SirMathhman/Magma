/*package magma;*//*

import java.util.ArrayList;*//*
import java.util.Collection;*//*
import java.util.stream.Stream;*//*

public record State(Collection<String> segments, StringBuilder buffer) {
	public State() {
		this(new ArrayList<>(), new StringBuilder());*//*
	}

	Stream<String> stream() {
		return this.segments.stream();*//*
	}

	State advance() {
		this.segments.add(this.buffer.toString());*//*
		this.buffer.setLength(0);*//*
		return this;*//*
	}

	State append(final char c) {
		this.buffer.append(c);*//*
		return this;*//*
	}
}*/