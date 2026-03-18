package runtime;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Integer> table = new HashMap<>();

    public void set(String name, int value) {
        table.put(name, value);
    }

    public int get(String name) {
        return table.getOrDefault(name, 0);
    }
}