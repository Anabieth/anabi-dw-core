package lv.llu.science.dwh.domain.metadata;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "metadata_report")
@Value
public class ReportMetadata {
    @Id
    String id;
    LocalDateTime lastUpdateTs;
}
