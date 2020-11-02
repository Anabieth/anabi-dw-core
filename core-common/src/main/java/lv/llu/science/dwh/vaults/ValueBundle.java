package lv.llu.science.dwh.vaults;

import lombok.Value;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

@Value
public class ValueBundle {
    private String id;
    private String element;

    public ValueBundle(String prefix, String bundle, String element) {
        this.id = prefix + ":" + bundle;
        this.element = element;
    }

    public static ValueBundle hourly(String prefix, ZonedDateTime ts) {
        ZonedDateTime utc = ts.withZoneSameInstant(UTC);
        String bundle = utc.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        int element = utc.getMinute();
        return new ValueBundle(prefix, bundle, String.format("%02d", element));
    }

    public static ValueBundle daily(String prefix, ZonedDateTime ts) {
        ZonedDateTime utc = ts.withZoneSameInstant(UTC);
        String bundle = utc.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int element = utc.getHour();
        return new ValueBundle(prefix, bundle, String.format("%02d", element));
    }


    public static ZonedDateTime getZonedDateTime(String timestamp, String element) {
        return getZonedDateTime(timestamp.concat(element));
    }

    public static ZonedDateTime getZonedDateTime(String timestamp) {
        switch (timestamp.length()) {
            case 12:
                return parseTimestamp(timestamp, "yyyyMMddHHmm");
            case 10:
                return parseTimestamp(timestamp, "yyyyMMddHH");
            case 8:
                return parseTimestamp(timestamp + "00", "yyyyMMddHH");
            default:
                throw new IllegalArgumentException("Unexpected bundle timestamp length.");
        }
    }

    private static ZonedDateTime parseTimestamp(String timestamp, String pattern) {
        return parse(timestamp, ofPattern(pattern).withZone(UTC));
    }
}
