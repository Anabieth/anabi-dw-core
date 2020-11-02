package lv.llu.science.dwh.vaults

import lv.llu.science.dwh.domain.input.InputSwamp
import lv.llu.science.dwh.domain.input.ObjectValue
import lv.llu.science.dwh.domain.messages.DataInMessage
import lv.llu.science.dwh.domain.messages.DwhTopicPublisher
import lv.llu.science.utils.time.TimeMachine
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZonedDateTime

class BasicDataInVaultSpec extends Specification {

    def operations = Mock(MongoOperations)
    def timeMachine = Mock(TimeMachine) {
        now() >> LocalDateTime.of(2019, 2, 1, 12, 57)
    }
    def publisher = Mock(DwhTopicPublisher)

    def vault = null

    def setup() {
        setupVault(VaultType.Scalar)
    }

    private void setupVault(VaultType type) {
        vault = new BasicDataInVault(operations, timeMachine, publisher, type, "testing_collection", "testingTopic")
    }

    def "should receive scalar data-in message"() {
        given:
            def ts = ZonedDateTime.parse('2018-11-29T13:55:24Z')
            def message = new DataInMessage('temperature', 'swamp123')
            def swamp = new InputSwamp(
                    objectId: 'object123',
                    values: [
                            new ObjectValue(ts.plusMinutes(0), 11.1, null),
                            new ObjectValue(ts.plusMinutes(2), 22.2, null),
                            new ObjectValue(ts.plusMinutes(42), 33.3, null),
                            new ObjectValue(ts.plusMinutes(62), 44.4, null)
                    ])

        when:
            vault.receiveDataInMessage(message)
        then:
            1 * operations.findAndModify(*_) >> swamp
            4 * operations.upsert(_ as Query, _ as Update, 'testing_collection')
            1 * publisher.sendDataFlow('temperature', 'hourly', 'object123:2018112913', ['55', '57'])
            1 * publisher.sendDataFlow('temperature', 'hourly', 'object123:2018112914', ['37', '57'])
            1 * operations.remove(swamp)
    }

    def "should receive array data-in message"() {
        given:
            setupVault(VaultType.Array)
            def ts = ZonedDateTime.parse('2018-11-29T13:55:24Z')
            def message = new DataInMessage('temperature', 'swamp123')
            def swamp = new InputSwamp(
                    objectId: 'object123',
                    values: [
                            new ObjectValue(ts.plusMinutes(0), null, [11.1f, 11.1f, 11.1f, 11.1f]),
                            new ObjectValue(ts.plusMinutes(2), null, [22.2f, 22.2f, 22.2f, 22.2f]),
                            new ObjectValue(ts.plusMinutes(42), null, [33.3f, 33.3f, 33.3f, 33.3f]),
                            new ObjectValue(ts.plusMinutes(62), null, [44.4f, 44.4f, 44.4f, 44.4f])
                    ])

        when:
            vault.receiveDataInMessage(message)
        then:
            1 * operations.findAndModify(*_) >> swamp
            4 * operations.upsert(_ as Query, _ as Update, 'testing_collection')
            1 * publisher.sendDataFlow('temperature', 'hourly', 'object123:2018112913', ['55', '57'])
            1 * publisher.sendDataFlow('temperature', 'hourly', 'object123:2018112914', ['37', '57'])
            1 * operations.remove(swamp)
    }

    def "should handle duplicate elements"() {
        given:
            def ts = ZonedDateTime.parse('2018-11-29T13:55:23Z')
            def message = new DataInMessage('temperature', 'swamp123')
            def swamp = new InputSwamp(
                    objectId: 'object123',
                    values: [
                            new ObjectValue(ts, 11.1, null),
                            new ObjectValue(ts.plusMinutes(2), 22.2, null),
                            new ObjectValue(ts.plusMinutes(42), 33.3, null),
                            new ObjectValue(ts.plusMinutes(62), 44.4, null)
                    ])

        when:
            vault.receiveDataInMessage(message)
        then:
            1 * operations.findAndModify(*_) >> swamp
            4 * operations.upsert(_ as Query, _ as Update, 'testing_collection') >> {
                throw new DuplicateKeyException("testing expection")
            }
            0 * publisher.sendDataFlow(*_)
            1 * operations.remove(swamp)
    }

    def "should handle non-existing swamp"() {
        given:
            def message = new DataInMessage('temperature', 'swamp234')
        when:
            vault.receiveDataInMessage(message)
        then:
            1 * operations.findAndModify(*_) >> null
            0 * operations.upsert(*_)
            0 * publisher.sendDataFlow(*_)
            0 * operations.remove(_)
    }

    def "should ignore empty values"() {
        given:
            def ts = ZonedDateTime.parse('2018-11-29T13:55:24Z')
            def message = new DataInMessage('temperature', 'swamp123')
            def swamp = new InputSwamp(
                    objectId: 'object123',
                    values: [
                            new ObjectValue(ts.plusMinutes(0), null, null),
                            new ObjectValue(ts.plusMinutes(2), null, null)
                    ])

        when:
            vault.receiveDataInMessage(message)
        then:
            1 * operations.findAndModify(*_) >> swamp
            0 * operations.upsert(*_)
            0 * publisher.sendDataFlow(*_)
    }

    def "should ignore empty swamp"() {
        given:
            def message = new DataInMessage('temperature', 'swamp123')
            def swamp = new InputSwamp(objectId: 'object123')

        when:
            vault.receiveDataInMessage(message)
        then:
            1 * operations.findAndModify(*_) >> swamp
            0 * operations.upsert(*_)
            0 * publisher.sendDataFlow(*_)
            1 * operations.remove(swamp)
    }

}
