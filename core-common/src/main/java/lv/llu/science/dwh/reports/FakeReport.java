package lv.llu.science.dwh.reports;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@Deprecated
@Profile("!prod")
public class FakeReport implements Report {

    @Override
    public String getReportCode() {
        return "fakeReport";
    }

    @Override
    public String getReportName() {
        return "Fake report for testing purposes";
    }

    @Override
    public ReportDataBean getReportData(String objectId, ZonedDateTime from, ZonedDateTime to, Integer limit) {
        return null;
    }
}
