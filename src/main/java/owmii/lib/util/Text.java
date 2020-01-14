package owmii.lib.util;

import java.text.NumberFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Text {
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public static String numFormat(long value) {
        if (value == Long.MIN_VALUE) return numFormat(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + numFormat(-value);
        if (value < 1000) return Long.toString(value);

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String addCommas(long value) {
        return NumberFormat.getInstance().format(value);
    }
}
