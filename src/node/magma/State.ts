/*package magma;*//*

import java.util.ArrayList;*//*
import java.util.Collection;*//*

public record State(Collection<String> segments, StringBuilder buffer) {
	public State() {
		this(new ArrayList<>(), new StringBuilder());*//*
	}

	State advance() {
		this.segments().add(this.buffer().toString());*//*
		this.buffer().setLength(0);*//*
		return this;*//*
	}

	State append(final char c) {
		this.buffer().append(c);*//*
		return this;*//*
	}
}*/