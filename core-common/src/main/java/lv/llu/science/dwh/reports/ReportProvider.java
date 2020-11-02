package lv.llu.science.dwh.reports;

import lombok.Getter;
import lv.llu.science.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Service
public class ReportProvider {

    @Getter
    final private Map<String, Report> reports = new HashMap<>();

    @Autowired
    public void setReports(List<Report> reports) {
        reports.forEach(r -> {
            if (this.reports.containsKey(r.getReportCode())) {
                throw new Error("Multiple reports with same code: " + r.getReportCode());
            }
            this.reports.put(r.getReportCode(), r);
        });
    }

    public Report get(String code) {
        return ofNullable(reports.get(code)).orElseThrow(() -> new NotFoundException("Report not found"));
    }

}
