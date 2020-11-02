package lv.llu.science.dwh.vaults


import org.springframework.jms.config.JmsListenerEndpointRegistrar
import spock.lang.Specification

class VaultsTopicConfiguratorSpec extends Specification {
    def "should configure jms listeners"() {
        given:
            def registrar = Mock(JmsListenerEndpointRegistrar)
            def configurator = new VaultsTopicConfigurator(null)

            configurator.setDataInVaults([
                    new BasicDataInVault(null, null, null,
                            VaultType.Scalar, "test", "topicA"),
                    new BasicDataInVault(null, null, null,
                            VaultType.Scalar, "test", "topicB")
            ])

        when:
            configurator.configureJmsListeners(registrar)
        then:
            1 * registrar.registerEndpoint({ it.destination == "swamp:topicA" })
            1 * registrar.registerEndpoint({ it.destination == "swamp:topicB" })
    }
}
