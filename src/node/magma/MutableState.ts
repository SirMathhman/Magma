/*package magma;*/
/*import java.util.ArrayList;*/
/*import java.util.Collections;*/
/*import java.util.List;*/
/*public */class MutableState /*implements State*/ {/*
    private final List<String> segments = new ArrayList<>();*//*
    private StringBuilder buffer = new StringBuilder();*//*
    private int depth = 0;*//*

    @Override
    public State advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }*//*

    @Override
    public State append(final char c) {
        this.buffer.append(c);
        return this;
    }*//*

    @Override
    public List<String> unwrap() {
        return Collections.unmodifiableList(this.segments);
    }*//*

    @Override
    public boolean isLevel() {
        return 0 == this.depth;
    }*//*

    @Override
    public State enter() {
        this.depth++;
        return this;
    }*//*

    @Override
    public State exit() {
        this.depth--;
        return this;
    }*//*

    @Override
    public boolean isShallow() {
        return 1 == depth;
    }*//*
*/}
/**/
