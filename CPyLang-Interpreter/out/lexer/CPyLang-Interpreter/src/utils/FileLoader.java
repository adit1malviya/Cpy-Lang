package utils;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLoader {

    public static String load(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            throw new RuntimeException("Error reading file");
        }
    }
}