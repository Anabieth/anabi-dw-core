package lv.llu.science.dwh.bees.vaults

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

import static lv.llu.science.dwh.vaults.VaultType.Array
import static lv.llu.science.dwh.vaults.VaultType.Scalar
import static org.springframework.data.mongodb.core.query.Criteria.where
import static org.springframework.data.mongodb.core.query.Query.query
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("embedded")
class AllVaultsIntegrationSpec extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper mapper

    @Autowired
    MongoOperations operations

    @Unroll
    def "should accept #topic data-in"() {
        given:
            def data = [
                    objectId: "obj-987",
                    type    : topic,
                    values  : [
                            [ts: "2019-05-21T10:45:00Z"]
                    ]
            ]

            if (vaultType == Array) {
                data.values[0].put("values", value)
            } else {
                data.values[0].put("value", value)
            }

        expect:
            mvc.perform(
                    post("/dwh/")
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(data)))
                    .andExpect(status().isOk())
        when:
            def conditions = new PollingConditions(timeout: 20, initialDelay: 1, delay: 1, factor: 1.5)

        then:
            conditions.eventually {
                assert operations.count(query(where("_id").is("obj-987:2019052110")), collectionName) == 1
            }

        where:
            topic         | collectionName       | vaultType | value
            "audio"       | "audio_hourly"       | Array     | [0.1, 0.2, 0.3]
            "humidity"    | "humidity_hourly"    | Scalar    | 75.6
            "temperature" | "temperature_hourly" | Scalar    | 25.5
            "weight"      | "weight_hourly"      | Scalar    | 125.1
            "voltage"     | "voltage_hourly"     | Scalar    | 3860
    }

}
