package lv.llu.science.dwh.domain;

import lv.llu.science.dwh.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModelService {

    private final ModelProvider modelProvider;

    @Autowired
    public ModelService(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public List<ModelTemplate> getModelList() {
        return modelProvider.getModels().values().stream()
                .map(Model::getTemplate)
                .collect(Collectors.toList());
    }

    public void saveModelDefinition(String code, ModelDefinition definition) {
        modelProvider.get(code).saveModelDefinition(definition);
    }

    public void deleteModelDefinition(String code, String id) {
        modelProvider.get(code).deleteModelDefinition(id);
    }

    public List<ModelLatestValue<?>> getObjectLatestValues(String objectId) {
        return modelProvider.getModels().values().stream()
                .map(model -> model.getLatestModelValue(objectId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
