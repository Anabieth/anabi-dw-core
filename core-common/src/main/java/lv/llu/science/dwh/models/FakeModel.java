package lv.llu.science.dwh.models;

import lombok.Getter;
import lv.llu.science.dwh.reports.ReportDataBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;

@Component
@Deprecated
@Profile("!prod")
public class FakeModel implements Model {
    @Getter
    private final ModelTemplate template = ModelTemplate.builder()
            .code("fakeModel")
            .name("Fake model for testing purposes")
            .description("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt " +
                    "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
                    "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                    "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                    "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
            .param(ModelTemplateParam.builder()
                    .code("fakeParamA")
                    .name("First fake param")
                    .type(ModelTemplateParamType.nodeId)
                    .description("Aenean volutpat, dolor in hendrerit pulvinar, elit justo convallis ex, id blandit purus ipsum sit amet erat.")
                    .build())
            .param(ModelTemplateParam.builder()
                    .code("fakeParamB")
                    .name("Second fake param")
                    .type(ModelTemplateParamType.nodeId)
                    .description("Cras id tempor urna. Maecenas aliquet eu velit a placerat. Duis vel leo libero.")
                    .build())
            .param(ModelTemplateParam.builder()
                    .code("fakeParamC")
                    .name("Third fake param")
                    .type(ModelTemplateParamType.number)
                    .description("Vestibulum eleifend accumsan leo, nec vestibulum libero facilisis sit amet.")
                    .build())
            .build();

    @Override
    public void saveModelDefinition(ModelDefinition definition) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteModelDefinition(String definition) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReportDataBean getReportData(String objectId, ZonedDateTime from, ZonedDateTime to, Integer limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ModelLatestValue<?>> getLatestModelValue(String objectId) {
        ModelLatestValue<Object> latestValue = new ModelLatestValue<>();
        latestValue.setTimestamp(ZonedDateTime.now());
        latestValue.setModelCode(template.getCode());
        latestValue.setLabel("fake");
        latestValue.setDescription("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt");
        return Optional.of(latestValue);
    }
}
