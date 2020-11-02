package lv.llu.science.utils.time


import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest

import static TimeFilter.TIME_HEADER

class TimeFilterSpec extends Specification {
    def "should use mocked time header"() {
        given:
            def timeMachine = Mock(RequestTimeMachine)
            def filter = new TimeFilter(timeMachine)
            def request = Stub(HttpServletRequest) {
                getHeader(TIME_HEADER) >> "2017-03-08T12:13:14"
            }
        when:
            filter.doFilter(request, null, Stub(FilterChain))

        then:
            1 * timeMachine.fixedAt(_)
    }
}
