package lv.llu.science.dwh.vaults

import lv.llu.science.dwh.domain.messages.DataFlowMessage
import lv.llu.science.dwh.domain.messages.DwhTopicPublisher
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import spock.lang.Specification

import static org.springframework.data.mongodb.core.query.Criteria.where
import static org.springframework.data.mongodb.core.query.Query.query

class CommonDataDailyVaultSpec extends Specification {

    def operations = Mock(MongoOperations)
    def publisher = Mock(DwhTopicPublisher)

    def vault = new CommonDataDailyVault() {
        @Override
        void receiveMessage(DataFlowMessage message) {}
    }

    def setup() {
        vault.setOperations(operations)
        vault.setPublisher(publisher)
    }

    def "should receive data flow message"() {
        given:
            def message = new DataFlowMessage(type: 'temperature', bundleType: 'hourly',
                    bundleId: 'obj123:2018113009', elements: ['10', '15', '17'])
            def srcBundle = new DataBundle(id: 'obj123:2018113009', sum: 150, count: 25)
            def targetBundle = new DataBundle(id: 'obj123:20181130', values: ['09': 12.3f])

            def newDataQuery = query(where("_id").is('obj123:20181130'))
        when:
            vault.receiveDataFlowMessage(message)
        then:
            1 * operations.findOne(_ as Query, _ as Class, 'temperature_hourly') >> srcBundle
            1 * operations.findAndModify(newDataQuery, _ as Update, _ as FindAndModifyOptions, _ as Class, 'temperature_daily') >> targetBundle
            1 * operations.updateFirst(newDataQuery, _ as Update, 'temperature_daily')
            1 * publisher.sendDataFlow('temperature', 'daily', 'obj123:20181130', ['09'])
    }

    def "should handle wrong id"() {
        given:
            def message = new DataFlowMessage(type: 'temperature', bundleType: 'hourly',
                    bundleId: 'obj123:2018113009', elements: ['10', '15', '17'])
        when:
            vault.receiveDataFlowMessage(message)
        then:
            1 * operations.findOne(_ as Query, _ as Class, 'temperature_hourly') >> null
            0 * operations./.*/(*_)
    }

    def "should handle failed update"() {
        given:
            def message = new DataFlowMessage(type: 'temperature', bundleType: 'hourly',
                    bundleId: 'obj123:2018113009', elements: ['10', '15', '17'])
            def srcBundle = new DataBundle(id: 'obj123:2018113009', sum: 150, count: 25)
            operations.findOne(*_) >> srcBundle
            operations.findAndModify(*_) >> null
        when:
            vault.receiveDataFlowMessage(message)
        then:
            thrown(RuntimeException)

    }
}
