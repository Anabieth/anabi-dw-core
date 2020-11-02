package lv.llu.science.dwh.domain

import lv.llu.science.dwh.models.*
import spock.lang.Specification

class ModelServiceSpec extends Specification {

    def provider = Mock(ModelProvider)
    def service = new ModelService(provider)

    def "should get model list"() {
        given:
            def t1 = ModelTemplate.builder().code('modelA').build()
            def t2 = ModelTemplate.builder().code('modelB').build()
        when:
            def list = service.getModelList()
        then:
            1 * provider.getModels() >> [
                    'A': Stub(Model) { getTemplate() >> t1 },
                    'B': Stub(Model) { getTemplate() >> t2 }
            ]
            list == [t1, t2]
    }

    def "should save model"() {
        given:
            def model = Mock(Model)
            def definition = new ModelDefinition()
        when:
            service.saveModelDefinition('modelA', definition)
        then:
            1 * provider.get('modelA') >> model
            1 * model.saveModelDefinition(definition)
    }

    def "should delete model"() {
        given:
            def model = Mock(Model)
        when:
            service.deleteModelDefinition('modelA', 'id123')
        then:
            1 * provider.get('modelA') >> model
            1 * model.deleteModelDefinition('id123')
    }

    def "should get object latest values"() {
        given:
            def modelA = Mock(Model)
            def modelB = Mock(Model)
        when:
            def res = service.getObjectLatestValues('obj123')
        then:
            1 * provider.getModels() >> [a: modelA, b: modelB]
            1 * modelA.getLatestModelValue('obj123') >> Optional.of(new ModelLatestValue<>(modelCode: 'A', rawValue: 123))
            1 * modelB.getLatestModelValue('obj123') >> Optional.empty()
            res[0].modelCode == 'A'
            res[0].rawValue == 123
    }
}
