package lv.llu.science.dwh.vaults;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document
public class DataBundle extends BundleBase {
    private Integer count;
    private Float max;
    private Float min;
    private Float sum;
    private Map<String, Float> values;

    public Float getAverage() {
        return sum / count;
    }
}

