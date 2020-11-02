package lv.llu.science.dwh.models;

import lombok.Getter;
import lv.llu.science.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Service
public class ModelProvider {

    @Getter
    private final Map<String, Model> models = new HashMap<>();

    @Autowired
    public void setModels(List<Model> models) {
        models.forEach(m -> {
            String code = m.getTemplate().getCode();
            if (this.models.containsKey(code)) {
                throw new Error("Multiple models with same code: " + code);
            }
            this.models.put(code, m);
        });
    }

    public Model get(String code) {
        return ofNullable(models.get(code)).orElseThrow(() -> new NotFoundException("Model not found: " + code));
    }

}
