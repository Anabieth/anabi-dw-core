package lv.llu.science.utils.time

import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static java.time.Month.MARCH
import static java.time.temporal.ChronoUnit.DAYS
import static java.time.temporal.ChronoUnit.SECONDS

class TimeMachineSpec extends Specification {

    def machine = new TimeMachine()

    def "should get current date and time"() {
        expect:
            machine.now().until(LocalDateTime.now(), SECONDS) == 0
    }

    def "should get current date"() {
        expect:
            machine.today().until(LocalDate.now(), DAYS) == 0
    }

    def "should switch to fixed clock"() {
        def fixedTime = LocalDateTime.of(2017, MARCH, 8, 10, 11, 12)
        when:
            machine.fixedAt(fixedTime)
        then:
            machine.now() == fixedTime
    }
}
