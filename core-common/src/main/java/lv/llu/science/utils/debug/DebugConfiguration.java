package lv.llu.science.utils.debug;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("debug")
@ComponentScan("lv.llu.science.utils.debug")
public interface DebugConfiguration {
}
