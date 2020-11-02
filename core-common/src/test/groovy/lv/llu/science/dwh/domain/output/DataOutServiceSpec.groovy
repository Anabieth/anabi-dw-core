package lv.llu.science.dwh.domain.output

import lv.llu.science.dwh.reports.Report
import lv.llu.science.dwh.reports.ReportDataBean
import lv.llu.science.dwh.reports.ReportProvider
import spock.lang.Specification

import java.time.ZonedDateTime

class DataOutServiceSpec extends Specification {

    def provider = Mock(ReportProvider)
    def service = new DataOutService(provider)

    def "should get report list"() {
        given:
            def rep1 = Stub(Report) {
                getReportCode() >> "first"
                getReportName() >> "First report"
            }

            def rep2 = Stub(Report) {
                getReportCode() >> "second"
                getReportName() >> "Second report"
            }

            provider.getReports() >> ["first": rep1, "second": rep2]
        when:
            def list = service.getReportList()
        then:
            list.size() == 2
            list.collect { it.code } == ["first", "second"]
            list.collect { it.name } == ["First report", "Second report"]
    }

    def "should get report details"() {
        given:
            def report = Mock(Report) {
                getReportCode() >> "first"
                getReportName() >> "First report"
            }
            provider.get("first") >> report
        when:
            def bean = service.getReportDetails('first')
        then:
            bean.name == "First report"
            bean.code == "first"
    }

    def "should get report data"() {
        given:
            def from = ZonedDateTime.now().minusHours(10)
            def to = ZonedDateTime.now().minusHours(1)
            def report = Mock(Report) {
                getReportCode() >> "first"
                getReportName() >> "First report"
            }

            provider.get("first") >> report
        when:
            def bean = service.getReportData('first', 'farm-123', from, to, 1234)
        then:
            1 * report.getReportData("farm-123", from, to, 1234) >> new ReportDataBean()
            bean.name == "First report"
            bean.code == "first"
    }
}
