package lv.llu.science.dwh.models


import lv.llu.science.utils.exceptions.NotFoundException
import spock.lang.Specification

class ModelProviderSpec extends Specification {

    def provider = new ModelProvider()

    def "should create model map"() {
        given:
            def model1 = Stub(Model) {
                getTemplate() >> Stub(ModelTemplate) {
                    getCode() >> "aaa"
                }
            }
            def model2 = Stub(Model) {
                getTemplate() >> Stub(ModelTemplate) {
                    getCode() >> "bbb"
                }
            }
        when:
            provider.setModels([model1, model2])
        then:
            provider.get("aaa") == model1
            provider.get("bbb") == model2
    }

    def "should throw on duplicate codes"() {
        given:
            def model1 = Stub(Model) {
                getTemplate() >> Stub(ModelTemplate) {
                    getCode() >> "aaa"
                }
            }
            def model2 = Stub(Model) {
                getTemplate() >> Stub(ModelTemplate) {
                    getCode() >> "aaa"
                }
            }
        when:
            provider.setModels([model1, model2])
        then:
            thrown(Error)
    }

    def "should throw when report not found"() {
        when:
            provider.get("invalid-code")
        then:
            thrown(NotFoundException)
    }
}
