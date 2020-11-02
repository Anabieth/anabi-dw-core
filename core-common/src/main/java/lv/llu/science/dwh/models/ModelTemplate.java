package lv.llu.science.dwh.models;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ModelTemplate {
    private String code;
    private String name;
    private String description;
    @Singular
    private List<ModelTemplateParam> params;
}
