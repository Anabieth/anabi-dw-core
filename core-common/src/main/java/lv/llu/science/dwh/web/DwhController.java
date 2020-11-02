package lv.llu.science.dwh.web;

import lv.llu.science.dwh.domain.ModelService;
import lv.llu.science.dwh.domain.input.DataInResultBean;
import lv.llu.science.dwh.domain.input.DataInService;
import lv.llu.science.dwh.domain.input.ObjectValuesBean;
import lv.llu.science.dwh.domain.output.DataOutService;
import lv.llu.science.dwh.models.ModelDefinition;
import lv.llu.science.dwh.models.ModelTemplate;
import lv.llu.science.dwh.reports.ReportBean;
import lv.llu.science.dwh.reports.ReportDataBean;
import lv.llu.science.dwh.vaults.BasicDataInVault;
import lv.llu.science.dwh.vaults.VaultsTopicConfigurator;
import lv.llu.science.utils.exceptions.BadRequestException;
import lv.llu.science.utils.time.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("dwh")
public class DwhController {

    private final static int MAX_LIMIT = 1_000_000;

    private final DataInService dataInService;
    private final DataOutService dataOutService;
    private final TimeMachine timeMachine;
    private final VaultsTopicConfigurator vaults;
    private final ModelService modelService;

    @Autowired
    public DwhController(DataInService dataInService, DataOutService dataOutService, TimeMachine timeMachine, VaultsTopicConfigurator vaults, ModelService modelService) {
        this.dataInService = dataInService;
        this.dataOutService = dataOutService;
        this.timeMachine = timeMachine;
        this.vaults = vaults;
        this.modelService = modelService;
    }

    @PostMapping
    public DataInResultBean storeData(@RequestBody ObjectValuesBean body) {
        return dataInService.storeData(body);
    }

    @GetMapping("topics")
    public List<String> getSupportedDataInTopics() {
        return vaults.getDataInVaults().stream()
                .map(BasicDataInVault::getDataInTopic)
                .collect(toList());
    }

    @GetMapping("reports")
    public List<ReportBean> getReportList() {
        return dataOutService.getReportList();
    }

    @GetMapping("reports/{code}")
    ReportBean getReportDetails(@PathVariable String code) {
        return dataOutService.getReportDetails(code);
    }

    @GetMapping(path = "reports/{code}/{objectId}")
    public ReportDataBean getReportData(@PathVariable String code,
                                        @PathVariable String objectId,
                                        @RequestParam(value = "from", required = false)
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                ZonedDateTime from,
                                        @RequestParam(value = "to", required = false)
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                ZonedDateTime to,
                                        @RequestParam(value = "limit", required = false)
                                                Integer limit) {

        Integer lim = ofNullable(limit).orElse(MAX_LIMIT);

        if (lim < 1 || lim > MAX_LIMIT) {
            throw new BadRequestException("Record limit should be in range from 1 to " + MAX_LIMIT);
        }

        return dataOutService.getReportData(code, objectId,
                ofNullable(from).orElse(timeMachine.zonedNow().minusWeeks(1)),
                ofNullable(to).orElse(timeMachine.zonedNow()),
                lim);
    }

    @GetMapping("models")
    public List<ModelTemplate> getModelList() {
        return modelService.getModelList();
    }

    @PostMapping("models/{id}")
    public void defineModel(@PathVariable("id") String code, @RequestBody ModelDefinition bean) {
        modelService.saveModelDefinition(code, bean);
    }

    @DeleteMapping("models/{code}/{id}")
    void removeModel(@PathVariable("code") String code, @PathVariable("id") String id) {
        modelService.deleteModelDefinition(code, id);
    }

    @GetMapping("models/latest/{objectId}")
    public Object getObjectModelLatestValues(@PathVariable String objectId) {
        return modelService.getObjectLatestValues(objectId);
    }
}
