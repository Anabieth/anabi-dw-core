package lv.llu.science.dwh.vaults

import spock.lang.Specification

import java.time.ZonedDateTime

class ValueBundleSpec extends Specification {
    def "should create hourly bundle"() {
        when:
            def bundle = ValueBundle.hourly('testPrefix', ZonedDateTime.parse('2019-01-29T07:08:09Z'))
        then:
            bundle.id == 'testPrefix:2019012907'
            bundle.element == '08'
    }

    def "should create daily bundle"() {
        when:
            def bundle = ValueBundle.daily('testPrefix', ZonedDateTime.parse('2019-01-29T07:08:09Z'))
        then:
            bundle.id == 'testPrefix:20190129'
            bundle.element == '07'
    }

    def "should convert timestamp string to ZonedDateTime"() {
        expect:
            ValueBundle.getZonedDateTime(input) == result
        where:
            input          || result
            '201902011516' || ZonedDateTime.parse('2019-02-01T15:16:00Z')
            '2019020115'   || ZonedDateTime.parse('2019-02-01T15:00:00Z')
            '20190201'     || ZonedDateTime.parse('2019-02-01T00:00:00Z')
    }

    def "should thrown exception when converting illegal pattern"() {
        when:
            ValueBundle.getZonedDateTime("2019")
        then:
            thrown(IllegalArgumentException)
    }
}
