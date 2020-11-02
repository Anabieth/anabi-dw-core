package lv.llu.science.dwh.domain.input

import lv.llu.science.dwh.domain.messages.DwhTopicPublisher
import lv.llu.science.dwh.domain.metadata.EventType
import lv.llu.science.dwh.domain.metadata.MetadataService
import lv.llu.science.utils.time.TimeMachine
import spock.lang.Specification

import java.time.LocalDateTime

class DataInServiceSpec extends Specification {

    def metadataService = Mock(MetadataService)
    def repository = Mock(InputSwampRepository)
    def publisher = Mock(DwhTopicPublisher)
    def timeMachine = Mock(TimeMachine)
    def service = new DataInService(metadataService, repository, publisher, timeMachine)

    def "should store incoming data"() {
        given:
            def bean = new ObjectValuesBean(objectId: 'obj-123',
                    type: 'datatype',
                    values: [new ObjectValue(timeMachine.zonedNow(), 123.45, null)])
            def time = LocalDateTime.of(2018, 6, 5, 10, 20, 30)
        when:
            def resultBean = service.storeData(bean)
        then:
            1 * timeMachine.now() >> time
            1 * repository.save(_) >> { args ->
                def swamp = args[0] as InputSwamp
                assert swamp.status == InputSwampStatus.Initial
                assert swamp.objectId == bean.objectId
                assert swamp.values == bean.values
                assert swamp.createdTs == time
                swamp.id = "abc123"
                swamp
            }
            1 * metadataService.registerEvent(EventType.DataIn, ["swampId": "abc123"])
            1 * publisher.sendDataIn('datatype', 'abc123')
            resultBean.swampId == "abc123"
    }

    def "should start input swamp processing"() {
        when:
            service.startSwampProcessing()
        then:
            1 * repository.findAll(_) >> [
                    new InputSwamp(type: 'new'),
                    new InputSwamp(type: 'another new')]
            2 * publisher.sendDataIn(*_)
    }
}
