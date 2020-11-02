package lv.llu.science.dwh.reports;

import java.time.ZonedDateTime;

public interface Report {
    String getReportCode();
    String getReportName();
    ReportDataBean getReportData(String objectId, ZonedDateTime from, ZonedDateTime to, Integer limit);
}
