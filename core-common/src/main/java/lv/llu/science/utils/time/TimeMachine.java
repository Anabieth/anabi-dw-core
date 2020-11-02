package lv.llu.science.utils.time;

import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class TimeMachine {

    private final ZoneId defaultZone = ZoneId.systemDefault();

    private Clock clock = Clock.systemDefaultZone();

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public ZonedDateTime zonedNow() {
        return ZonedDateTime.now(clock);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public void fixedAt(LocalDateTime time) {
        this.clock = Clock.fixed(time.atZone(defaultZone).toInstant(), defaultZone);
    }


    public static ZonedDateTime toZonedTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault());
    }
}
