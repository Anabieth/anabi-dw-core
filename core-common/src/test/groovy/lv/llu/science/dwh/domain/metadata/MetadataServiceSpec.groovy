package lv.llu.science.dwh.domain.metadata

import com.mongodb.BasicDBObject
import lv.llu.science.utils.time.TimeMachine
import org.springframework.data.mongodb.core.MongoOperations
import spock.lang.Specification

import java.time.LocalDateTime

import static lv.llu.science.dwh.domain.metadata.EventType.DataIn
import static lv.llu.science.dwh.domain.metadata.EventType.ReportUpdated

class MetadataServiceSpec extends Specification {

    def timeMachine = Stub(TimeMachine) {
        now() >> LocalDateTime.now()
    }
    def operations = Mock(MongoOperations)
    def service = new MetadataService(operations, timeMachine)

    def "should register event"() {
        when:
            service.registerEvent(DataIn, new BasicDBObject(['meta': 'data']))
        then:
            1 * operations.save(_ as Event)

        when:
            service.registerEvent(ReportUpdated, new BasicDBObject(['reportId': '123']))
        then:
            1 * operations.save(_ as Event)
            1 * operations.save(_ as ReportMetadata)
    }

    def "should get report metadata"() {
        when:
            service.getReportMetadata("report-123")
        then:
            1 * operations.findById("report-123", ReportMetadata.class)
    }
}
