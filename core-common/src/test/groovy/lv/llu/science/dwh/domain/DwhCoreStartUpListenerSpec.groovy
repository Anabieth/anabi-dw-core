package lv.llu.science.dwh.domain

import lv.llu.science.dwh.domain.input.DataInService
import spock.lang.Specification

class DwhCoreStartUpListenerSpec extends Specification {
    def dataInService = Mock(DataInService)
    def listener = new DwhCoreStartUpListener(dataInService)

    def "should start swamp processing"() {
        when:
            listener.onStartUp(null)
        then:
            1 * dataInService.startSwampProcessing()
    }
}
