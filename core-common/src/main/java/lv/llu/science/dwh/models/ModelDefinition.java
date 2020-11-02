package lv.llu.science.dwh.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Map;

@Data
public class ModelDefinition {
    @Id
    private String id;
    private Map<String, Object> params;
}
