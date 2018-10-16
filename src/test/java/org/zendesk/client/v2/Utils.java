package org.zendesk.client.v2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    public static String resourceToString(String filePath) throws URISyntaxException, IOException {
        Path path = Paths.get(Utils.class.getClassLoader().getResource(filePath).toURI());
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines.collect(Collectors.joining("\n"));
        }
    }
}
