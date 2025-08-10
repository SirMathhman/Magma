/*package magma;*//*

import java.util.Collection;*//*
import java.util.stream.Stream;*//*

public class State {
	public final Collection<String> segments;*//*
	private StringBuilder buffer;*//*

	public State(Collection<String> segments, StringBuilder buffer) {
		this.segments = segments;*//*
		this.buffer = buffer;*//*
	}

	Stream<String> stream() {
		return segments.stream();*//*
	}

	State advance() {
		segments.add(buffer.toString());*//*
		this.buffer = new StringBuilder();*//*
		return this;*//*
	}

	State append(char c) {
		buffer.append(c);*//*
		return this;*//*
	}
}
*/