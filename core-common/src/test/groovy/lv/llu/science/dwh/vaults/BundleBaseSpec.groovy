package lv.llu.science.dwh.vaults

import spock.lang.Specification

class BundleBaseSpec extends Specification {
    def bundle = new BundleBase(id: 'testingObject:2019012915')

    def "should return object id"() {
        expect:
            bundle.getObjectId() == "testingObject"
    }

    def "should return bundle"() {
        expect:
            bundle.getTimestamp() == "2019012915"
    }
}
