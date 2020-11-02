package lv.llu.science.dwh.vaults;

import lombok.Getter;
import lombok.extern.java.Log;
import lv.llu.science.dwh.domain.messages.DataInMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.List;

import static java.text.MessageFormat.format;

@Service
@Log
public class VaultsTopicConfigurator implements JmsListenerConfigurer {

    private final MappingJackson2MessageConverter converter;
    @Getter
    private List<BasicDataInVault> dataInVaults;

    @Autowired
    public VaultsTopicConfigurator(MappingJackson2MessageConverter converter) {
        this.converter = converter;
    }

    @Autowired
    public void setDataInVaults(List<BasicDataInVault> dataInVaults) {
        this.dataInVaults = dataInVaults;
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        for (BasicDataInVault vault : dataInVaults) {
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            endpoint.setId("data-in-" + vault.getDataInTopic());
            endpoint.setDestination("swamp:" + vault.getDataInTopic());
            endpoint.setMessageListener(message -> {
                try {
                    DataInMessage payload = (DataInMessage) converter.fromMessage(message);
                    vault.receiveDataInMessage(payload);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });
            registrar.registerEndpoint(endpoint);
            log.info(format("Registered data-in topic: {0}", vault.getDataInTopic()));
        }
    }
}
