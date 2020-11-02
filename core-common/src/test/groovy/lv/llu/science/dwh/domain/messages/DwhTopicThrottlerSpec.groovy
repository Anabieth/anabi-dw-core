package lv.llu.science.dwh.domain.messages


import lv.llu.science.utils.time.TimeMachine
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

import java.util.logging.Level
import java.util.logging.Logger

class DwhTopicThrottlerSpec extends Specification {

    def operations = Mock(MongoOperations)
    def timeMachine = new TimeMachine()
    def jmsTemplate = Mock(JmsTemplate)
    def throttler = new DwhTopicThrottler(operations, timeMachine, jmsTemplate)

    def "should send data flow messages"() {
        given:
            Logger logger = ReflectionTestUtils.getField(throttler, 'log') as Logger
            logger.setLevel(Level.OFF) // no spamming in tests
            def msg = new DataFlowMessage(type: 'temperature', bundleType: 'daily')
        when:
            throttler.sendDataFlowMessages()
        then:
            2000 * operations.findAndRemove(*_) >> msg
            2000 * jmsTemplate.convertAndSend('temperature:daily', msg)
    }
}
