package lv.llu.science.dwh.reports

import lv.llu.science.utils.exceptions.NotFoundException
import spock.lang.Specification

class ReportProviderSpec extends Specification {
    def provider = new ReportProvider()

    def "should create report map"() {
        given:
            def report1 = Stub(Report) {
                getReportCode() >> "aaa"
            }
            def report2 = Stub(Report) {
                getReportCode() >> "bbb"
            }
        when:
            provider.setReports([report1, report2])
        then:
            provider.get("aaa") == report1
            provider.get("bbb") == report2
    }

    def "should throw on duplicate codes"() {
        given:
            def report1 = Stub(Report) {
                getReportCode() >> "aaa"
            }
            def report2 = Stub(Report) {
                getReportCode() >> "aaa"
            }
        when:
            provider.setReports([report1, report2])
        then:
            thrown(Error)
    }

    def "should throw when report not found"() {
        when:
            provider.get("invalid-code")
        then:
            thrown(NotFoundException)
    }
}
