package lv.llu.science.dwh.vaults;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class CompoundModelBundle<T> extends BundleBase {
    Map<String, T> values;
}
