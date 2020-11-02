package lv.llu.science.dwh.bees;

import lv.llu.science.dwh.DwhCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(DwhCore.class)
public class BeesDwhCore {
    public static void main(String[] args) {
        SpringApplication.run(BeesDwhCore.class, args);
    }
}