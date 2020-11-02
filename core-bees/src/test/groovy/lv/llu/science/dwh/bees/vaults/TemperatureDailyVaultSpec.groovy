package lv.llu.science.dwh.bees.vaults

import lv.llu.science.dwh.domain.messages.DataFlowMessage
import spock.lang.Specification

class TemperatureDailyVaultSpec extends Specification {
    def "should receive message"() {
        given:
            def msg = new DataFlowMessage(type: 'temperature', bundleType: 'hourly', bundleId: 'id123', elements: ['1', '2', '3'])
            def vault = Spy(TemperatureDailyVault)
        when:
            vault.receiveMessage(msg)
        then:
            1 * vault.receiveDataFlowMessage(msg) >> null
    }
}
