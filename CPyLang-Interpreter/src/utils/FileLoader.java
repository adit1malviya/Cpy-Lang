package utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class FileLoader {
    public static String load(String path) {
        String code = "";
        try {
            File file = new File(path);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                code = code + sc.nextLine() + "\n";
            }
            sc.close();
        } 
        catch (FileNotFoundException e) {
        System.out.println("File not found");
        }
        return code;
    }
}