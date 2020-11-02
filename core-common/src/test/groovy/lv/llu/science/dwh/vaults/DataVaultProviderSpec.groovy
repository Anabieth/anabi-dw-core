package lv.llu.science.dwh.vaults

import spock.lang.Specification

class DataVaultProviderSpec extends Specification {

    def provider = new DataVaultProvider()

    def "should create vault map"() {
        given:
            def vault1 = Stub(DataVault) {
                provides() >> "aaa"
            }
            def vault2 = Stub(DataVault) {
                provides() >> "bbb"
            }
        when:
            provider.setVaults([vault1, vault2])
        then:
            provider.get("aaa") == vault1
            provider.get("bbb") == vault2
    }

    def "should throw on duplicate topics"() {
        given:
            def vault1 = Stub(DataVault) {
                provides() >> "aaa"
            }

            def vault2 = Stub(DataVault) {
                provides() >> "aaa"
            }
        when:
            provider.setVaults([vault1, vault2])
        then:
            thrown(Error)
    }
}
