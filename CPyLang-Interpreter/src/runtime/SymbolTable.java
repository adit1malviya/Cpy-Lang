package runtime;

import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, Object> table;

    public SymbolTable() {
        table = new HashMap<>();
    }

    public void set(String name, Object value) {
        table.put(name, value);
    }

    public Object get(String name) {
        Object value = table.get(name);

        if (value == null) {
            return 0;
        }

        return value;
    }
}