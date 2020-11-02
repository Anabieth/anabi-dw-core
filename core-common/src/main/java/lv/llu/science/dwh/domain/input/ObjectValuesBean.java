package lv.llu.science.dwh.domain.input;

import lombok.Data;

import java.util.List;

@Data
public class ObjectValuesBean {
    private String objectId;
    private String type;
    private List<ObjectValue> values;
}
