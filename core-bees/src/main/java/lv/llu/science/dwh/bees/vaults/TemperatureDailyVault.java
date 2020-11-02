package lv.llu.science.dwh.bees.vaults;

import lv.llu.science.dwh.domain.messages.DataFlowMessage;
import lv.llu.science.dwh.vaults.CommonDataDailyVault;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TemperatureDailyVault extends CommonDataDailyVault {

    @Override
    @JmsListener(destination = "temperature:hourly")
    public void receiveMessage(@Payload DataFlowMessage message) {
        receiveDataFlowMessage(message);
    }
}
