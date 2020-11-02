package lv.llu.science.dwh


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification


@SpringBootTest
@ActiveProfiles("embedded")
@Import(TestingVaultConfig.class)
class DwhCoreSpec extends Specification {

    @Autowired
    WebApplicationContext context

    def "should start DWH core context"() {
        expect:
            context != null
    }
}
