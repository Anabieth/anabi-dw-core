package lv.llu.science.dwh.bees.reports;

import lv.llu.science.dwh.reports.BasicReport;
import lv.llu.science.dwh.reports.BasicReportFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfig {

    private BasicReportFactory factory = new BasicReportFactory();

    @Bean
    public BasicReport temperatureReport() {
        return factory.createBasicReport("temperature", "Raw temperature measurements", "temperature_hourly");
    }

    @Bean
    public BasicReport humidityReport() {
        return factory.createBasicReport("humidity", "Raw humidity measurements", "humidity_hourly");
    }

    @Bean
    public BasicReport weightReport() {
        return factory.createBasicReport("weight", "Raw weight measurements", "weight_hourly");
    }

    @Bean
    public BasicReport voltageReport() {
        return factory.createBasicReport("voltage", "Raw voltage measurements", "voltage_hourly");
    }
}
