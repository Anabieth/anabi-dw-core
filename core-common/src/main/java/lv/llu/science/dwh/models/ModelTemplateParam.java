package lv.llu.science.dwh.models;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ModelTemplateParam {
    private String code;
    private String name;
    private String description;
    private ModelTemplateParamType type;
    private Boolean master;
    @Singular
    private List<String> options;
}
