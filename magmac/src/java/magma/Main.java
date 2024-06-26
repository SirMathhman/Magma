package magma;

import magma.api.json.CompoundJSONParser;
import magma.api.json.JSONArrayParser;
import magma.api.json.JSONObjectParser;
import magma.api.json.JSONStringParser;
import magma.api.json.JSONValue;
import magma.api.json.LazyJSONParser;
import magma.api.option.Option;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.compile.CompileException;
import magma.java.JavaList;
import magma.java.JavaResults;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static magma.java.JavaResults.$Void;

public class Main {
    public static final Path CONFIG_PATH = Paths.get(".", "config.json");

    public static void main(String[] args) {
        var result = run();
        if (result.isPresent()) {
            //noinspection CallToPrintStackTrace
            result.orElsePanic().printStackTrace();
        }
    }

    private static Option<CompileException> run() {
        return $Void(() -> {
            var configuration = JavaResults.$Result(buildConfiguration().mapErr(CompileException::new));
            JavaResults.$Option(new Application(configuration).run());
        });
    }

    private static Result<Configuration, IOException> buildConfiguration() {
        try {
            var absolutePath = CONFIG_PATH.toAbsolutePath();
            if (Files.exists(CONFIG_PATH)) {
                System.out.printf("Found configuration file at '%s'.%n", absolutePath);
            } else {
                System.out.printf("Configuration file did not exist and will be created at '%s'.%n", absolutePath);
                Files.writeString(CONFIG_PATH, "{}");
            }

            var configurationString = Files.readString(CONFIG_PATH);

            var valueParser = new LazyJSONParser();
            valueParser.setValue(new CompoundJSONParser(new JavaList<>(List.of(
                    new JSONStringParser(),
                    new JSONArrayParser(valueParser),
                    new JSONObjectParser(valueParser)
            ))));

            var parsedOption = valueParser.parse(configurationString);
            if (parsedOption.isEmpty()) {
                return new Err<>(new IOException("Failed to parse configuration: " + configurationString));
            }

            var parsed = parsedOption.orElsePanic();

            System.out.println("Parsed configuration.");
            System.out.println(parsed);

            var builds = parsed.find("builds").orElsePanic();
            var map = builds.stream().orElsePanic().map(build -> {
                var sources = buildSet(build, "sources");
                var targets = buildSet(build, "targets");
                var debug = Path.of(build.find("debug")
                        .orElsePanic()
                        .findValue()
                        .orElsePanic());

                return new Build(sources, targets, debug);
            }).collect(JavaList.collecting());

            return new Ok<>(new Configuration(map));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static BuildSet buildSet(JSONValue build, String key) {
        var root = build.find(key).orElsePanic();
        var location = root.find("location").orElsePanic().findValue().orElsePanic();
        var platform = root.find("platform").orElsePanic().findValue().orElsePanic();
        return new BuildSet(Path.of(location), platform);
    }
}
