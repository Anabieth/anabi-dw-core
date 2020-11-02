package lv.llu.science.dwh.bees.vaults;

import lv.llu.science.dwh.domain.messages.DwhTopicPublisher;
import lv.llu.science.dwh.vaults.BasicDataInVault;
import lv.llu.science.utils.time.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;

import static lv.llu.science.dwh.vaults.VaultType.Array;
import static lv.llu.science.dwh.vaults.VaultType.Scalar;

@Configuration
public class DataInVaultConfig {

    private final MongoOperations operations;
    private final TimeMachine timeMachine;
    private final DwhTopicPublisher publisher;

    @Autowired
    public DataInVaultConfig(MongoOperations operations, TimeMachine timeMachine, DwhTopicPublisher publisher) {
        this.operations = operations;
        this.timeMachine = timeMachine;
        this.publisher = publisher;
    }

    @Bean
    public BasicDataInVault audioVault() {
        return new BasicDataInVault(operations, timeMachine, publisher, Array, "audio_hourly", "audio");
    }

    @Bean
    public BasicDataInVault temperatureVault() {
        return new BasicDataInVault(operations, timeMachine, publisher, Scalar, "temperature_hourly", "temperature");
    }

    @Bean
    public BasicDataInVault humidityVault() {
        return new BasicDataInVault(operations, timeMachine, publisher, Scalar, "humidity_hourly", "humidity");
    }

    @Bean
    public BasicDataInVault weightVault() {
        return new BasicDataInVault(operations, timeMachine, publisher, Scalar, "weight_hourly", "weight");
    }

    @Bean
    public BasicDataInVault voltageVault(){
        return new BasicDataInVault(operations, timeMachine, publisher, Scalar,"voltage_hourly","voltage");
    }
}
