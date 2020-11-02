package lv.llu.science.dwh.models;

import lv.llu.science.dwh.reports.Report;

import java.util.Optional;

public interface Model extends Report {
    ModelTemplate getTemplate();

    void saveModelDefinition(ModelDefinition definition);

    void deleteModelDefinition(String definition);

    @Override
    default String getReportCode() {
        return getTemplate().getCode();
    }

    @Override
    default String getReportName() {
        return getTemplate().getName();
    }

    default Optional<ModelLatestValue<?>> getLatestModelValue(String objectId) {
        return Optional.empty();
    }
}
