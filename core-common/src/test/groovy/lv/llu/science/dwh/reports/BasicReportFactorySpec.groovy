package lv.llu.science.dwh.reports

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import spock.lang.Specification

import java.time.ZonedDateTime

class BasicReportFactorySpec extends Specification {
    def "should create basic report"() {
        given:
            def factory = new BasicReportFactory()
            def from = ZonedDateTime.now().minusDays(20)
            def to = ZonedDateTime.now()
            def operations = Mock(MongoOperations)
            def converter = Mock(ReportItemConverter)
        when:
            def report = factory.createBasicReport('testCode', 'test name', 'test_collection')
        then:
            report.getReportCode() == 'testCode'
            report.getReportName() == 'test name'
        when:
            report.setOperations(operations)
            report.setConverter(converter)
            report.getReportData('obj123', from, to, 1234)
        then:
            1 * operations.aggregate(_, 'test_collection', ReportItem.class) >> Stub(AggregationResults)
            1 * converter.apply(_)
    }
}
