package lv.llu.science.dwh.bees.models

import com.fasterxml.jackson.databind.ObjectMapper
import lv.llu.science.dwh.models.ModelDefinition
import lv.llu.science.dwh.vaults.CompoundModelBundle
import lv.llu.science.dwh.vaults.DataBundle
import lv.llu.science.dwh.vaults.ValueBundle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.time.ZonedDateTime

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.data.mongodb.core.query.Criteria.where
import static org.springframework.data.mongodb.core.query.Query.query
import static org.springframework.data.mongodb.core.query.Update.update
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles(["embedded", "testing"])
class SimpleTemperatureDeltaModelIntegrationSpec extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper mapper

    @Autowired
    MongoOperations operations

    @Autowired
    SimpleTemperatureDeltaModel model

    def setup() {
        // prepare model definition
        def map = [tempIn : 'objIn',
                   tempOut: 'objOut',
                   delta  : 10.0f]

        def definition = new ModelDefinition(id: 'modelX', params: map)
        model.saveModelDefinition(definition)

    }

    def cleanup() {
        operations.getCollectionNames()
                .forEach({ name -> operations.dropCollection(name) })
    }

    def "should process model"() {
        given:
            operations.save(
                    new DataBundle(id: 'objOut:2020052010',
                            values: [
                                    '10': 21.1f,
                                    '20': 22.2f,
                                    '30': 23.3f,
                                    '40': 24.4f,
                            ]
                    ),
                    'temperature_hourly')

            def data = [
                    objectId: 'objIn',
                    type    : 'temperature',
                    values  : [
                            [ts: '2020-05-20T10:11:00Z', value: 35.5f],
                            [ts: '2020-05-20T10:21:00Z', value: 34.2f],
                            [ts: '2020-05-20T10:31:00Z', value: 25.2f],
                    ]
            ]

        expect:
            mvc.perform(
                    post("/dwh/")
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(data)))
                    .andExpect(status().isOk())

        when:
            def conditions = new PollingConditions(timeout: 10, initialDelay: 1, delay: 1, factor: 1.5)

        then:
            conditions.eventually {
                def one = operations.findOne(
                        query(where("_id").is("objIn:2020052010")),
                        CompoundModelBundle.class,
                        'simple_temperature_model_data')

                assert one.values['11'].state == 'ok'
                assert one.values['21'].state == 'ok'
                assert one.values['31'].state == 'nok'
            }
    }

    def "should get model report and latest values"() {
        given:
            def now = ZonedDateTime.now()
            for (int i = 0; i < 200; i++) {
                def bundle = ValueBundle.hourly('modelX', now.minusMinutes(i))
                operations.upsert(
                        query(where('_id').is(bundle.getId())),
                        update('values.' + bundle.getElement(), [state: 'ok', tempIn: 35f, tempOut: 22f]),
                        'simple_temperature_model_data'
                )
            }
        when:
            def result = model.getReportData("modelX", ZonedDateTime.now().minusDays(2), ZonedDateTime.now(), 100)
        then:
            result.data.size() == 4
        then:
            def latest = model.getLatestModelValue('modelX')
        then:
            with(latest.get().rawValue) {
                state == 'ok'
                tempIn == 35.0
                tempOut == 22.0
            }


    }

    def "should work end to end"() {
        given:
            def conditions = new PollingConditions(timeout: 10, initialDelay: 1, delay: 1, factor: 1.5)
        when:
            def now = ZonedDateTime.now()
            [['objIn', 'objOut'], 0..50].combinations { it ->
                def data = [
                        objectId: it[0],
                        type    : 'temperature',
                        values  : [
                                [ts: now.minusMinutes(it[1] * 2), value: Math.random() * 15 + 20]
                        ]
                ]
                mvc.perform(
                        post("/dwh/")
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(data)))
                        .andExpect(status().isOk())
            }

        then:
            conditions.eventually {
                mvc.perform(
                        get("/dwh/reports/simpleTemperatureDelta/objIn" +
                                "?from=${now.minusDays(1).format(ISO_ZONED_DATE_TIME)}" +
                                "&to=${now.format(ISO_ZONED_DATE_TIME)}" +
                                "&limit=30"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath('$.data', hasSize(4)))
                        .andExpect(jsonPath('$.data[0].values', hasSize(30)))
                        .andExpect(jsonPath('$.data[1].type', is('category')))
                        .andExpect(jsonPath('$.data[1].categories', is(['ok', 'nok'])))
            }
    }

}
