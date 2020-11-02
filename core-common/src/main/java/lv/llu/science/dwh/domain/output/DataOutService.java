package lv.llu.science.dwh.domain.output;

import lv.llu.science.dwh.reports.Report;
import lv.llu.science.dwh.reports.ReportBean;
import lv.llu.science.dwh.reports.ReportDataBean;
import lv.llu.science.dwh.reports.ReportProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataOutService {

    private final ReportProvider reportProvider;

    @Autowired
    public DataOutService(ReportProvider reportProvider) {
        this.reportProvider = reportProvider;
    }

    public List<ReportBean> getReportList() {
        return reportProvider.getReports().values().stream()
                .map(report -> setReportCommonValues(report, new ReportBean()))
                .collect(Collectors.toList());
    }

    public ReportDataBean getReportData(String code, String objectId,
                                        ZonedDateTime from,
                                        ZonedDateTime to,
                                        Integer limit) {
        Report report = reportProvider.get(code);
        ReportDataBean bean = report.getReportData(objectId, from, to, limit);
        setReportCommonValues(report, bean);
        return bean;
    }

    private ReportBean setReportCommonValues(Report report, ReportBean bean) {
        bean.setCode(report.getReportCode());
        bean.setName(report.getReportName());
        return bean;
    }

    public ReportBean getReportDetails(String code) {
        Report report = reportProvider.get(code);
        ReportBean bean = new ReportBean();
        setReportCommonValues(report, bean);
        return bean;
    }
}
