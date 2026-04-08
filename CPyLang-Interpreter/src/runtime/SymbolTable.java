package runtime;
import java.util.HashMap;
public class SymbolTable {
    private HashMap<String, Integer> table;
    public SymbolTable() {
        table = new HashMap<>();
    }
    public void set(String name, int value) {
        table.put(name, value);
    }
    public int get(String name) {
        Integer value = table.get(name);
        if (value == null) {
            return 0;
        }
        return value;
    }
}