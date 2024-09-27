package agile18;

public class Utils {
    private Utils() {}

    public static boolean isEmpty(String... strings) {
        for (String str : strings) {
            if (str == null || str.isEmpty()) return true;
        }
        return false;
    }

    public static String sqlValues(Object... values) {
        StringBuilder total = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            Object val = values[i];
            if (val instanceof String s) {
                total.append("'");
                total.append(s);
                total.append("'");
            }
            
            if (i != values.length-1) {
                total.append(',');
            }
        }
        return total.toString();
    }
}
