package lv.llu.science.dwh.reports

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.aggregation.*
import spock.lang.Specification

import java.time.ZonedDateTime

class BasicReportSpec extends Specification {

    def operations = Mock(MongoOperations)
    BasicReport report

    def setup() {
        report = new BasicReport() {
            @Override
            String getReportCode() {
                return "basic"
            }

            @Override
            String getReportName() {
                return "basic name"
            }

            @Override
            ReportDataBean getReportData(String objectId, ZonedDateTime from, ZonedDateTime to, Integer limit) {
                return null
            }
        }

        report.setOperations(operations)
    }

    def "should get report items"() {
        given:
            def to = ZonedDateTime.now()
            def from = to.minusHours(5)
        when:
            report.getReportItems('testing_collection', 'obj-123', from, to, 1234)
        then:
            1 * operations.aggregate(
                    { Aggregation agg ->
                        agg.operations.collect { it.class } == [
                                MatchOperation,
                                ProjectionOperation,
                                UnwindOperation,
                                ProjectionOperation,
                                MatchOperation,
                                SampleOperation,
                                SortOperation
                        ]
                    },
                    'testing_collection', ReportItem.class) >> Stub(AggregationResults)
    }
}
