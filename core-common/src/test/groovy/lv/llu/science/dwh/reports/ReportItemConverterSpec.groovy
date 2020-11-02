package lv.llu.science.dwh.reports

import spock.lang.Specification

import java.time.ZonedDateTime

class ReportItemConverterSpec extends Specification {
    def converter = new ReportItemConverter()

    def "should convert report items to bean"() {
        given:
            def ts = ZonedDateTime.now()
            def ts1 = ts.minusMinutes(1)
            def ts2 = ts.minusMinutes(2)
            def ts3 = ts.minusMinutes(3)

            def items = [new ReportItem(id: ts1, values: [a: 10f, b: 15f]),
                         new ReportItem(id: ts2, values: [a: 20f, b: 25f]),
                         new ReportItem(id: ts3, values: [a: 30f, b: 35f])]
        when:
            def result = converter.apply(items)
        then:
            result.data.collect { it.name } == ['timestamp', 'a', 'b']

    }
}
