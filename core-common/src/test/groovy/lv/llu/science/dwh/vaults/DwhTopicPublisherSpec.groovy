package lv.llu.science.dwh.vaults

import lv.llu.science.dwh.domain.messages.DataFlowMessage
import lv.llu.science.dwh.domain.messages.DataInMessage
import lv.llu.science.dwh.domain.messages.DwhTopicPublisher
import lv.llu.science.utils.time.TimeMachine
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.jms.core.JmsTemplate
import spock.lang.Specification

import java.time.ZonedDateTime

class DwhTopicPublisherSpec extends Specification {
    def jms = Mock(JmsTemplate)
    def operations = Mock(MongoOperations)
    def timeMachine = Mock(TimeMachine)
    def publisher = new DwhTopicPublisher(jms, operations, timeMachine)

    def "should send data-in message"() {
        when:
            publisher.sendDataIn("temp", "swamp-123")
        then:
            1 * jms.convertAndSend("swamp:temp", _ as DataInMessage)
    }

    def "should send data-flow message"() {
        given:
            def now = ZonedDateTime.parse('2020-03-31T10:20:30Z')
            timeMachine.zonedNow() >> now
        when:
            publisher.sendDataFlow("temp", "hourly", "obj123:20181129", ['10', '12', '15', '29'])
        then:
            1 * operations.upsert(
                    { Query q ->
                        def obj = q.getQueryObject()
                        assert obj.type == 'temp'
                        assert obj.bundleType == 'hourly'
                        assert obj.bundleId == 'obj123:20181129'
                        true
                    },
                    { Update u ->
                        def obj = u.getUpdateObject()
                        assert obj.'$set' == ['updateTs': now]
                        assert obj.'$addToSet'.'elements'.values == ['10', '12', '15', '29']
                        true
                    },
                    DataFlowMessage.class
            )
    }


}
