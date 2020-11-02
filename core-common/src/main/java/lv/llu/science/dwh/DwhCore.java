package lv.llu.science.dwh;

import lv.llu.science.utils.debug.DebugConfiguration;
import lv.llu.science.utils.time.TimeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Import({TimeConfiguration.class, DebugConfiguration.class})
public class DwhCore {
    public static void main(String[] args) {
        SpringApplication.run(DwhCore.class, args);
    }
}