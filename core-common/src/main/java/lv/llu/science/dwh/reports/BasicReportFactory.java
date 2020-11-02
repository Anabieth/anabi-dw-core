package lv.llu.science.dwh.reports;

import java.time.ZonedDateTime;
import java.util.List;

public class BasicReportFactory {
    public BasicReport createBasicReport(String code, String name, String collection) {
        return new BasicReport() {
            @Override
            public String getReportCode() {
                return code;
            }

            @Override
            public String getReportName() {
                return name;
            }

            @Override
            public ReportDataBean getReportData(String objectId, ZonedDateTime from, ZonedDateTime to, Integer limit) {
                List<ReportItem> result = getReportItems(collection, objectId, from, to, limit);
                return converter.apply(result);
            }
        };
    }
}
