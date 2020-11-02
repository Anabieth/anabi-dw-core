package lv.llu.science.dwh.reports;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
public class ReportItem {
    private ZonedDateTime id;
    private Map<String, Float> values;
}
