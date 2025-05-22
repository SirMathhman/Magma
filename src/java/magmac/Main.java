package magmac;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main() {
        try {
            var umlContent = "@startuml\nclass Main {\n}\n@enduml";
            var diagramPath = Paths.get(".", "diagram.puml");
            Files.writeString(diagramPath, umlContent);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
