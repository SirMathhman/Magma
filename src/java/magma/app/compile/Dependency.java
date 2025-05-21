package magma.app.compile;

public record Dependency(String name, String child) {
    public String toPlantUML() {
        return name() + " --> " + child() + "\n";
    }
}
