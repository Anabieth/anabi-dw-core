package lv.llu.science.dwh.bees.vaults

import lv.llu.science.dwh.domain.messages.DataFlowMessage
import spock.lang.Specification

class HumidityDailyVaultSpec extends Specification {
    def "should receive message"() {
        given:
            def msg = new DataFlowMessage(type: 'humidity', bundleType: 'hourly', bundleId: 'id123', elements: ['1', '2', '3'])
            def vault = Spy(HumidityDailyVault)
        when:
            vault.receiveMessage(msg)
        then:
            1 * vault.receiveDataFlowMessage(msg) >> null
    }
}
