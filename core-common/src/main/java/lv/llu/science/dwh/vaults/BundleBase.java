package lv.llu.science.dwh.vaults;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BundleBase {
    @Id
    protected String id;

    public String getObjectId() {
        return id.substring(0, id.indexOf(':'));
    }

    public String getTimestamp() {
        return id.substring(id.indexOf(':') + 1);
    }
}
