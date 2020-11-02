package lv.llu.science.dwh.domain.metadata;

import com.mongodb.DBObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "metadata_event")
@Data
public class Event {
    @Id
    private String id;
    private LocalDateTime eventTs;
    private EventType type;
    private DBObject metadata;
}
