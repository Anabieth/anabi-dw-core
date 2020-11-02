package lv.llu.science.dwh.domain.messages;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

@Document
@Data
@CompoundIndexes({
        @CompoundIndex(def = "{ 'type': 1, 'bundleType': 1, 'bundleId': 1}", unique = true)
})
public class DataFlowMessage {
    private String type;
    private String bundleType;
    private String bundleId;
    private List<String> elements;
    @Indexed
    private ZonedDateTime updateTs;
}
