package lv.llu.science.dwh.web

import lv.llu.science.dwh.domain.ModelService
import lv.llu.science.dwh.domain.input.DataInService
import lv.llu.science.dwh.domain.input.ObjectValuesBean
import lv.llu.science.dwh.domain.output.DataOutService
import lv.llu.science.dwh.models.ModelDefinition
import lv.llu.science.dwh.vaults.BasicDataInVault
import lv.llu.science.dwh.vaults.VaultType
import lv.llu.science.dwh.vaults.VaultsTopicConfigurator
import lv.llu.science.utils.exceptions.BadRequestException
import lv.llu.science.utils.time.TimeMachine
import spock.lang.Specification
import spock.lang.Unroll

import static java.time.ZonedDateTime.parse

class DwhControllerSpec extends Specification {

    def dataInService = Mock(DataInService)
    def dataOutService = Mock(DataOutService)
    def timeMachine = Mock(TimeMachine)
    def vaultConfig = Mock(VaultsTopicConfigurator)
    def modelService = Mock(ModelService)
    def controller = new DwhController(dataInService, dataOutService, timeMachine, vaultConfig, modelService)

    static dates = [
            now    : parse('2019-02-01T11:28:36Z'),
            weekAgo: parse('2019-01-25T11:28:36Z'),
            from   : parse('2018-06-01T00:00:00Z'),
            to     : parse('2018-11-30T00:00:00Z')
    ]

    def "should store data in DWH"() {
        when:
            controller.storeData(new ObjectValuesBean())
        then:
            1 * dataInService.storeData(_ as ObjectValuesBean)
    }

    def "should get list of reports"() {
        when:
            controller.getReportList()
        then:
            1 * dataOutService.getReportList()
    }

    def "should get report details"() {
        when:
            controller.getReportDetails('testing-report')
        then:
            1 * dataOutService.getReportDetails('testing-report')
    }

    @Unroll
    def "should get report data with #label parameters"() {
        given:
            timeMachine.zonedNow() >> dates.now
        when:
            controller.getReportData('rep1', 'obj123', from, to, limit)
        then:
            1 * dataOutService.getReportData('rep1', 'obj123', rFrom, rTo, rLimit)
        where:
            label     | from       | to       | limit || rFrom         | rTo       | rLimit
            'default' | null | null | null || dates.weekAgo | dates.now | 1_000_000
            'various' | dates.from | dates.to | 1234  || dates.from    | dates.to  | 1234

    }

    def "should throw exception for incorrect report data limit"() {
        when:
            controller.getReportData('rep1', 'obj123', null, null, limit)
        then:
            thrown(BadRequestException)
        where:
            limit << [0, 1_000_001]

    }

    def "should get supported data-in topics"() {
        when:
            def topics = controller.getSupportedDataInTopics()
        then:
            1 * vaultConfig.getDataInVaults() >> [
                    new BasicDataInVault(null, null, null, VaultType.Scalar, "test", "topicA"),
                    new BasicDataInVault(null, null, null, VaultType.Scalar, "test", "topicB"),
            ]
            topics == ["topicA", "topicB"]
    }

    def "should list models"() {
        when:
            controller.getModelList()
        then:
            1 * modelService.getModelList()
    }

    def "should define model"() {
        given:
            def definition = new ModelDefinition()
        when:
            controller.defineModel('modelA', definition)
        then:
            1 * modelService.saveModelDefinition('modelA', definition)
    }

    def "should remove model"() {
        when:
            controller.removeModel('modelA', 'id123')
        then:
            1 * modelService.deleteModelDefinition('modelA', 'id123')
    }

    def "should get object model latest values"() {
        when:
            controller.getObjectModelLatestValues('id123')
        then:
            1 * modelService.getObjectLatestValues('id123')
    }

}
