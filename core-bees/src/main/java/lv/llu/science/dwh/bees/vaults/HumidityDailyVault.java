package lv.llu.science.dwh.bees.vaults;

import lv.llu.science.dwh.domain.messages.DataFlowMessage;
import lv.llu.science.dwh.vaults.CommonDataDailyVault;
import org.springframework.jms.annotation.JmsListener;

public class HumidityDailyVault extends CommonDataDailyVault {

    @Override
    @JmsListener(destination = "humidity:hourly")
    public void receiveMessage(DataFlowMessage message) {
        receiveDataFlowMessage(message);
    }
}
