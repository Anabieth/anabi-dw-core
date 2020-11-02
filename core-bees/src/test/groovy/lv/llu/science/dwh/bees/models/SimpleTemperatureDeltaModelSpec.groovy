package lv.llu.science.dwh.bees.models

import lv.llu.science.dwh.models.ModelDefinition
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import spock.lang.Specification

class SimpleTemperatureDeltaModelSpec extends Specification {

    def operations = Mock(MongoOperations)
    def model = new SimpleTemperatureDeltaModel(operations)

    def "should save model definition"() {
        given:
            def definition = new ModelDefinition()
        when:
            model.saveModelDefinition(definition)
        then:
            1 * operations.save(definition, 'simple_temperature_model')
    }

    def "should delete model definition"() {
        when:
            model.deleteModelDefinition('model123')
        then:
            1 * operations.remove({ Query q -> q.getQueryObject()._id == 'model123' }, 'simple_temperature_model')
    }
}
