package lv.llu.science.dwh.domain.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectValue {
    private ZonedDateTime ts;
    private Float value;
    private List<Float> values;
}
