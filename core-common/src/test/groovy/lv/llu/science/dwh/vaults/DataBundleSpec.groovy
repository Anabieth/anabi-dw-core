package lv.llu.science.dwh.vaults

import spock.lang.Specification

class DataBundleSpec extends Specification {
    def bundle = new DataBundle(id: 'testingObject:2019012915', sum: 1234.56f, count: 4)

    def "should calculate average"() {
        expect:
            bundle.getAverage() == 308.64f
    }
}
